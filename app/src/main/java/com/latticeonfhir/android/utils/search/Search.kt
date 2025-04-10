package com.latticeonfhir.core.utils.search

import com.latticeonfhir.core.data.local.model.search.SearchParameters
import com.latticeonfhir.android.data.local.roomdb.entities.patient.PatientAndIdentifierEntity
import com.latticeonfhir.core.utils.converters.responseconverter.TimeConverter.toAge
import me.xdrop.fuzzywuzzy.FuzzySearch

object Search {

    internal fun getFuzzySearchList(
        totalList: List<PatientAndIdentifierEntity>,
        searchParameters: SearchParameters,
        matchingRatio: Int
    ): List<PatientAndIdentifierEntity> {
        var finalList = totalList.toMutableList()
        searchParameters.run {
            if (!gender.isNullOrBlank()) {
                finalList = finalList.filter {
                    gender == it.patientEntity.gender
                }.toMutableList()
            }
            if (!name.isNullOrBlank()) {
                finalList = finalList.filter {
                    val fullName =
                        "${it.patientEntity.firstName}${it.patientEntity.middleName ?: ""}${it.patientEntity.lastName ?: ""}"
                    FuzzySearch.weightedRatio(
                        name.replace(" ", "").trim(),
                        fullName
                    ) > matchingRatio
                }.toMutableList()
            }
            if (!patientId.isNullOrBlank()) {
                finalList = finalList.filter {
                    FuzzySearch.weightedRatio(
                        patientId,
                        it.patientEntity.fhirId ?: ""
                    ) > matchingRatio
                }.toMutableList()
            }
            if (minAge != null && maxAge != null) {
                finalList = finalList.filter {
                    (minAge <= it.patientEntity.birthDate.toAge()) && (it.patientEntity.birthDate.toAge() <= maxAge)
                }.toMutableList()
            }
            if (!addressLine1.isNullOrBlank()) {
                finalList = finalList.filter {
                    FuzzySearch.weightedRatio(
                        addressLine1,
                        it.patientEntity.permanentAddress.addressLine1
                    ) > matchingRatio
                }.toMutableList()
            }
            if (!city.isNullOrBlank()) {
                finalList = finalList.filter {
                    FuzzySearch.weightedRatio(
                        city,
                        it.patientEntity.permanentAddress.city
                    ) > matchingRatio
                }.toMutableList()
            }
            if (!district.isNullOrBlank()) {
                finalList = finalList.filter {
                    FuzzySearch.weightedRatio(
                        district,
                        it.patientEntity.permanentAddress.district ?: ""
                    ) > matchingRatio
                }.toMutableList()
            }
            if (!state.isNullOrBlank()) {
                finalList = finalList.filter {
                    FuzzySearch.weightedRatio(
                        state,
                        it.patientEntity.permanentAddress.state
                    ) > matchingRatio
                }.toMutableList()
            }
            if (!postalCode.isNullOrBlank()) {
                finalList = finalList.filter {
                    FuzzySearch.weightedRatio(
                        postalCode,
                        it.patientEntity.permanentAddress.postalCode
                    ) > matchingRatio
                }.toMutableList()
            }
            if (!addressLine2.isNullOrBlank()) {
                finalList = finalList.filter {
                    FuzzySearch.weightedRatio(
                        addressLine2,
                        it.patientEntity.permanentAddress.addressLine2 ?: ""
                    ) > matchingRatio
                }.toMutableList()
            }
        }
        return finalList
    }

    internal fun getFuzzySearchMedicationList(
        queryActiveIngredient: String,
        activeIngredients: List<String>,
        matchingRatio: Int
    ): List<String> {
        return activeIngredients.filter { activeIngredient ->
            FuzzySearch.partialRatio(queryActiveIngredient, activeIngredient) > matchingRatio
        }
    }

    internal fun getFuzzySearchDiagnosisList(
        searchQuery: String,
        diagnosisList: List<String>,
        matchingRatio: Int
    ): List<String> {
        return diagnosisList.filter { diagnosis ->
            FuzzySearch.partialRatio(searchQuery, diagnosis) > matchingRatio
        }
    }

    internal fun getFuzzySearchSymptomsList(
        searchQuery: String,
        symptomsList: List<String>,
        matchingRatio: Int
    ): List<String> {
        return symptomsList.filter { symptoms ->
            FuzzySearch.partialRatio(searchQuery, symptoms) > matchingRatio
        }
    }
}
