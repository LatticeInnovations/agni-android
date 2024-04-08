package com.latticeonfhir.android.utils.search

import com.latticeonfhir.android.data.local.model.search.SearchParameters
import com.latticeonfhir.android.utils.constants.patient.IdentificationConstants
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toAge
import me.xdrop.fuzzywuzzy.FuzzySearch
import org.hl7.fhir.r4.model.Patient

object Search {

    internal fun getFuzzySearchMedicationList(
        queryActiveIngredient: String,
        activeIngredients: List<String>,
        matchingRatio: Int
    ): List<String> {
        return activeIngredients.filter { activeIngredient ->
            FuzzySearch.partialRatio(queryActiveIngredient, activeIngredient) > matchingRatio
        }
    }

    internal fun getFuzzySearchPatientList(
        totalList: List<Patient>,
        searchParameters: SearchParameters,
        matchingRatio: Int
    ): List<Patient> {
        var finalList = totalList.toMutableList()
        searchParameters.run {
            if (!gender.isNullOrBlank()) {
                finalList = finalList.filter { patient ->
                    gender == patient.gender.toCode()
                } as MutableList<Patient>
            }
            if (!name.isNullOrBlank()) {
                finalList = finalList.filter { patient ->
                    FuzzySearch.weightedRatio(
                        name.replace(" ", "").trim(),
                        patient.nameFirstRep.nameAsSingleString
                    ) > matchingRatio
                } as MutableList<Patient>
            }
            if (!patientId.isNullOrBlank()) {
                finalList = finalList.filter { patient ->
                    val identifier = patient.identifier.firstOrNull {
                        it.system == IdentificationConstants.LATTICE
                    }?.value ?: ""
                    FuzzySearch.weightedRatio(
                        patientId,
                        identifier
                    ) > matchingRatio
                } as MutableList<Patient>
            }
            if (minAge != null && maxAge != null) {
                finalList = finalList.filter {
                    (minAge <= it.birthDate.time.toAge()) && (it.birthDate.time.toAge() <= maxAge)
                } as MutableList<Patient>
            }
            if (!lastFacilityVisit.isNullOrBlank()) {
                finalList = finalList.filter {
                    true
                } as MutableList<Patient>
            }
            if (!addressLine1.isNullOrBlank()) {
                finalList = finalList.filter {
                    FuzzySearch.weightedRatio(
                        addressLine1,
                        it.addressFirstRep.line[0].value
                    ) > matchingRatio
                } as MutableList<Patient>
            }
            if (!city.isNullOrBlank()) {
                finalList = finalList.filter {
                    FuzzySearch.weightedRatio(
                        city,
                        it.addressFirstRep.city
                    ) > matchingRatio
                } as MutableList<Patient>
            }
            if (!district.isNullOrBlank()) {
                finalList = finalList.filter {
                    FuzzySearch.weightedRatio(
                        district,
                        it.addressFirstRep.district ?: ""
                    ) > matchingRatio
                } as MutableList<Patient>
            }
            if (!state.isNullOrBlank()) {
                finalList = finalList.filter {
                    FuzzySearch.weightedRatio(
                        state,
                        it.addressFirstRep.state
                    ) > matchingRatio
                } as MutableList<Patient>
            }
            if (!postalCode.isNullOrBlank()) {
                finalList = finalList.filter {
                    FuzzySearch.weightedRatio(
                        postalCode,
                        it.addressFirstRep.postalCode
                    ) > matchingRatio
                } as MutableList<Patient>
            }
            if (!addressLine2.isNullOrBlank()) {
                finalList = finalList.filter {
                    val line2 =
                        if (it.addressFirstRep.line.size > 1) it.addressFirstRep.line[1].value else ""
                    FuzzySearch.weightedRatio(
                        addressLine2,
                        line2
                    ) > matchingRatio
                } as MutableList<Patient>
            }
        }
        return finalList
    }
}
