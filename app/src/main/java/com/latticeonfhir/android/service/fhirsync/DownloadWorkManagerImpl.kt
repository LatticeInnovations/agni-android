package com.latticeonfhir.android.service.fhirsync

import android.content.SharedPreferences
import com.google.android.fhir.sync.DownloadWorkManager
import com.google.android.fhir.sync.SyncDataParams
import com.google.android.fhir.sync.download.DownloadRequest
import com.latticeonfhir.android.utils.fhirengine.DownloadWorkManagerUtils.affixLastUpdatedTimestamp
import com.latticeonfhir.android.utils.fhirengine.DownloadWorkManagerUtils.extractAndSaveLastUpdateTimestampToFetchFutureUpdates
import com.latticeonfhir.android.utils.fhirengine.DownloadWorkManagerUtils.getLasUpdateTimestamp
import com.latticeonfhir.android.utils.fhirengine.UrlBuilder.buildSyncUrls
import org.hl7.fhir.exceptions.FHIRException
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.ListResource
import org.hl7.fhir.r4.model.OperationOutcome
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

class DownloadWorkManagerImpl(private val sharedPreferences: SharedPreferences) : DownloadWorkManager {

    private val resourceTypeList = ResourceType.entries.map { it.name }
    private val urls = buildSyncUrls("-_lastUpdated", 1000, sharedPreferences)

    override suspend fun getNextRequest(): DownloadRequest? {
        var url = urls.poll() ?: return null
        val resourceTypeToDownload = ResourceType.fromCode(url.findAnyOf(resourceTypeList, ignoreCase = true)!!.second)
        sharedPreferences.getLasUpdateTimestamp(resourceTypeToDownload)?.let {
            url = affixLastUpdatedTimestamp(url, it)
        }
        return DownloadRequest.of(url)
    }

    override suspend fun getSummaryRequestUrls(): Map<ResourceType, String> {
        return urls.associate {
            ResourceType.fromCode(it.substringBefore("?")) to
                    it.plus("&${SyncDataParams.SUMMARY_KEY}=${SyncDataParams.SUMMARY_COUNT_VALUE}")
        }
    }

    override suspend fun processResponse(response: Resource): Collection<Resource> {
        // As per FHIR documentation :
        // If the search fails (cannot be executed, not that there are no matches), the
        // return value SHALL be a status code 4xx or 5xx with an OperationOutcome.
        // See https://www.hl7.org/fhir/http.html#search for more details.
        if (response is OperationOutcome) {
            throw FHIRException(response.issueFirstRep.diagnostics)
        }

        // If the resource returned is a List containing Patients, extract Patient references and fetch
        // all resources related to the patient using the $everything operation.
        if (response is ListResource) {
            for (entry in response.entry) {
                val reference = Reference(entry.item.reference)
                /** Sonarlint may throw code smell on .equals() but code was copied from FHIR SDK Official Repo*/
                if (reference.referenceElement.resourceType.equals(other = "Patient")) {
                    val patientUrl = "${entry.item.reference}/\$everything"
                    urls.add(patientUrl)
                }
            }
        }

        // If the resource returned is a Bundle, check to see if there is a "next" relation referenced
        // in the Bundle.link component, if so, append the URL referenced to list of URLs to download.
        if (response is Bundle) {
            val nextUrl = response.link.firstOrNull { component -> component.relation == "next" }?.url
            if (nextUrl != null) {
                urls.add(nextUrl)
            }
        }

        // Finally, extract the downloaded resources from the bundle.
        var bundleCollection: Collection<Resource> = mutableListOf()
        if (response is Bundle && response.type == Bundle.BundleType.SEARCHSET) {
            bundleCollection =
                response.entry
                    .map { it.resource }
                    .also { extractAndSaveLastUpdateTimestampToFetchFutureUpdates(it, sharedPreferences) }
        }
        return bundleCollection
    }
}