package com.heartcare.agni.utils.search

import com.heartcare.agni.data.local.enums.RiskCategoryEnum.Companion.getRiskRange
import com.heartcare.agni.data.local.model.search.SearchParameters
import com.heartcare.agni.data.local.roomdb.entities.patient.PatientAndIdentifierEntity
import com.heartcare.agni.utils.constants.IdentificationConstants.HOSPITAL_ID
import com.heartcare.agni.utils.constants.IdentificationConstants.NATIONAL_ID
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toAge
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
            if (!provinceId.isNullOrBlank()) {
                finalList = finalList.filter {
                    it.patientEntity.permanentAddress.province == provinceId
                }.toMutableList()
            }
            if (!areaCouncilId.isNullOrBlank()) {
                finalList = finalList.filter {
                    it.patientEntity.permanentAddress.areaCouncil == areaCouncilId
                }.toMutableList()
            }
            if (minAge != null && maxAge != null) {
                finalList = finalList.filter {
                    (minAge <= it.patientEntity.birthDate.toAge()) && (it.patientEntity.birthDate.toAge() <= maxAge)
                }.toMutableList()
            }
            if (!riskCategory.isNullOrBlank()) {
                finalList = finalList.filter {
                    it.cvdList.maxByOrNull { cvd -> cvd.createdOn }?.risk in getRiskRange(riskCategory)
                }.toMutableList()
            }
            if (!name.isNullOrBlank()) {
                finalList = finalList.filter {
                    val fullName =
                        "${it.patientEntity.firstName}${it.patientEntity.lastName}"
                    FuzzySearch.weightedRatio(
                        name.replace(" ", "").trim(),
                        fullName
                    ) > matchingRatio
                }.toMutableList()
            }
            if (!fhirId.isNullOrBlank()) {
                finalList = finalList.filter {
                    FuzzySearch.weightedRatio(
                        fhirId,
                        it.patientEntity.fhirId ?: ""
                    ) > matchingRatio
                }.toMutableList()
            }
            if (!heartcareId.isNullOrBlank()) {
                finalList = finalList.filter {
                    FuzzySearch.weightedRatio(
                        heartcareId,
                        it.patientEntity.heartcareId ?: ""
                    ) > matchingRatio
                }.toMutableList()
            }
            if (!hospitalId.isNullOrBlank()) {
                finalList = finalList.filter {
                    FuzzySearch.weightedRatio(
                        hospitalId,
                        it.identifiers.firstOrNull { id -> id.identifierType == HOSPITAL_ID }?.identifierNumber ?: ""
                    ) > matchingRatio
                }.toMutableList()
            }
            if (!nationalId.isNullOrBlank()) {
                finalList = finalList.filter {
                    FuzzySearch.weightedRatio(
                        nationalId,
                        it.identifiers.firstOrNull { id -> id.identifierType == NATIONAL_ID }?.identifierNumber ?: ""
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
