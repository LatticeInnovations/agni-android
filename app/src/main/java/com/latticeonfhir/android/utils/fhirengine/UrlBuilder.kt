package com.latticeonfhir.android.utils.fhirengine

import org.hl7.fhir.r4.model.ResourceType
import java.util.LinkedList

object UrlBuilder {

    private val urls = mutableListOf<String>()

    internal fun buildSyncUrls(sortBy: String?, count: Int): LinkedList<String> {
        urls.clear()
        urls.add(ResourceType.Organization.name)
        urls.add(ResourceType.Location.name)
        urls.add(ResourceType.Patient.name)
        urls.add(ResourceType.Person.name)
        urls.add(ResourceType.RelatedPerson.name)
        urls.add(ResourceType.Schedule.name)
        urls.add(ResourceType.Slot.name)
        urls.add(ResourceType.Appointment.name)
        urls.add(ResourceType.Encounter.name)
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