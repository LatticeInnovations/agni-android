package com.latticeonfhir.android.ui.fhirsdk

import com.google.android.fhir.sync.DownloadRequest
import com.google.android.fhir.sync.DownloadWorkManager
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import java.util.LinkedList

class DownloadWorkManagerImpl : DownloadWorkManager {
    private val urls = LinkedList(listOf("Patient"))

    override suspend fun getNextRequest(): DownloadRequest? {
        val url = urls.poll() ?: return null
        return DownloadRequest.of(url)
    }

    override suspend fun getSummaryRequestUrls() = mapOf<ResourceType, String>()

    override suspend fun processResponse(response: Resource): Collection<Resource> {
        var bundleCollection: Collection<Resource> = mutableListOf()
        if (response is Bundle && response.type == Bundle.BundleType.SEARCHSET) {
            bundleCollection = response.entry.map { it.resource }
        }
        return bundleCollection
    }
}