package com.latticeonfhir.android.utils.fhirengine

import android.content.SharedPreferences
import androidx.core.content.edit
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTimeZoneString
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

object DownloadWorkManagerUtils {

    internal fun affixLastUpdatedTimestamp(url: String, lastUpdated: String): String {
        var downloadUrl = url

        // Affix lastUpdate to a $everything query using _since as per:
        // https://hl7.org/fhir/operation-patient-everything.html
        if (downloadUrl.contains("\$everything")) {
            downloadUrl = "$downloadUrl?_since=$lastUpdated"
        }

        // Affix lastUpdate to non-$everything queries as per:
        // https://hl7.org/fhir/operation-patient-everything.html
        if (!downloadUrl.contains("\$everything")) {
            downloadUrl = "$downloadUrl&_lastUpdated=gt$lastUpdated"
        }

        // Do not modify any URL set by a server that specifies the token of the page to return.
        if (downloadUrl.contains("&page_token")) {
            downloadUrl = url
        }

        return downloadUrl
    }

    internal fun SharedPreferences.getLasUpdateTimestamp(resourceType: ResourceType): String? {
        return getString(resourceType.name, null)
    }

    internal fun extractAndSaveLastUpdateTimestampToFetchFutureUpdates(
        resources: List<Resource>,
        sharedPreferences: SharedPreferences
    ) {
        resources
            .groupBy { it.resourceType }
            .entries
            .map { map ->
                sharedPreferences.edit {
                    putString(map.key.name, map.value.maxOfOrNull { it.meta.lastUpdated }?.toTimeZoneString())
                    commit()
                }
            }
    }
}