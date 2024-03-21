package com.latticeonfhir.android.utils.fhirengine

import android.content.SharedPreferences
import com.latticeonfhir.android.data.local.sharedpreferences.PreferenceStorageImpl.Companion.PREF_LOCATION_FHIR_ID
import com.latticeonfhir.android.data.local.sharedpreferences.PreferenceStorageImpl.Companion.PREF_ORGANIZATION_FHIR_ID
import org.hl7.fhir.r4.model.ResourceType
import java.util.LinkedList

object UrlBuilder {

    private val urls = mutableListOf<String>()

    internal fun buildSyncUrls(
        sortBy: String?,
        count: Int,
        sharedPreferences: SharedPreferences
    ) : LinkedList<String> {
        urls.clear()
        val orgId = sharedPreferences.getString(PREF_ORGANIZATION_FHIR_ID, null)
        val locationId = sharedPreferences.getString(PREF_LOCATION_FHIR_ID, null)
        urls.add("${ResourceType.Organization.name}?_id=$orgId")
        urls.add("${ResourceType.Location.name}?organization=Organization/$orgId")
        urls.add(ResourceType.Patient.name)
        urls.add(ResourceType.Person.name)
        urls.add(ResourceType.RelatedPerson.name)
        urls.add("${ResourceType.Schedule.name}?actor=Location/$locationId")
        urls.add("${ResourceType.Slot.name}?schedule.actor=Location/$locationId")
        urls.add("${ResourceType.Appointment.name}?actor=Location/$locationId")
        urls.add("${ResourceType.Encounter.name}?appointment.location=Location/$locationId")
        urls.add(ResourceType.Medication.name)
        urls.add(ResourceType.MedicationRequest.name)
        if (!sortBy.isNullOrBlank()) urlWithSorting(sortBy)
        affixCount(count)
        return LinkedList(urls)
    }

    private fun urlWithSorting(sortBy: String) {
        for (i in 0 until urls.size) {
            urls[i] = urls[i] + if (urls[i].contains("?")) "&_sort=$sortBy" else "?_sort=$sortBy"
        }
    }

    private fun affixCount(count: Int) {
        for (i in 0 until urls.size) {
            urls[i] = urls[i] + if(urls[i].contains("?")) "&_count=$count" else "?_count=$count"
        }
    }
}