package com.latticeonfhir.android.utils.search

import com.latticeonfhir.android.data.local.model.SearchParameters
import com.latticeonfhir.android.data.local.roomdb.entities.PatientAndIdentifierEntity
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toAge
import me.xdrop.fuzzywuzzy.FuzzySearch

object Search {

    internal fun getFuzzySearchList(
        totalList: List<PatientAndIdentifierEntity>,
        searchParameters: SearchParameters,
        matchingRatio: Int
    ): List<PatientAndIdentifierEntity> {
        var finalList = totalList.toMutableList()
        searchParameters.run {
            if(!gender.isNullOrBlank()) {
                finalList = finalList.filter {
                    gender == it.patientEntity.gender
                } as MutableList<PatientAndIdentifierEntity>
            }
            if (!name.isNullOrBlank()) {
                finalList = finalList.filter {
                    FuzzySearch.ratio(name, it.patientEntity.firstName) > matchingRatio ||
                    FuzzySearch.ratio(name,it.patientEntity.middleName ?: "") > matchingRatio ||
                    FuzzySearch.ratio(name,it.patientEntity.lastName ?: "") > matchingRatio
                } as MutableList<PatientAndIdentifierEntity>
            }
            if (!patientId.isNullOrBlank()) {
                finalList = finalList.filter {
                    FuzzySearch.ratio(patientId, it.identifiers.find { identifier -> identifier.identifierNumber == patientId }?.identifierNumber ?: "") > matchingRatio
                } as MutableList<PatientAndIdentifierEntity>
            }
            if (minAge != null && maxAge != null) {
                finalList = finalList.filter {
                    (minAge <= it.patientEntity.birthDate.toAge()) && (it.patientEntity.birthDate.toAge() <= maxAge)
                } as MutableList<PatientAndIdentifierEntity>
            }
            if (!lastFacilityVisit.isNullOrBlank()) {
                finalList = finalList.filter {
                    true
                } as MutableList<PatientAndIdentifierEntity>
            }
            if (!addressLine1.isNullOrBlank()) {
                finalList = finalList.filter {
                    FuzzySearch.ratio(
                        addressLine1,
                        it.patientEntity.permanentAddress.addressLine1
                    ) > matchingRatio
                } as MutableList<PatientAndIdentifierEntity>
            }
            if (!city.isNullOrBlank()) {
                finalList = finalList.filter {
                    FuzzySearch.ratio(
                        city,
                        it.patientEntity.permanentAddress.city ?: ""
                    ) > matchingRatio
                } as MutableList<PatientAndIdentifierEntity>
            }
            if (!district.isNullOrBlank()) {
                finalList = finalList.filter {
                    FuzzySearch.ratio(
                        district,
                        it.patientEntity.permanentAddress.district ?: ""
                    ) > matchingRatio
                } as MutableList<PatientAndIdentifierEntity>
            }
            if (!state.isNullOrBlank()) {
                finalList = finalList.filter {
                    FuzzySearch.ratio(
                        state,
                        it.patientEntity.permanentAddress.state
                    ) > matchingRatio
                } as MutableList<PatientAndIdentifierEntity>
            }
            if (!postalCode.isNullOrBlank()) {
                finalList = finalList.filter {
                    FuzzySearch.ratio(
                        postalCode,
                        it.patientEntity.permanentAddress.postalCode
                    ) > matchingRatio
                } as MutableList<PatientAndIdentifierEntity>
            }
            if (!addressLine2.isNullOrBlank()) {
                finalList = finalList.filter {
                    FuzzySearch.ratio(
                        addressLine2,
                        it.patientEntity.permanentAddress.addressLine2 ?: ""
                    ) > matchingRatio
                } as MutableList<PatientAndIdentifierEntity>
            }
        }
        return finalList
    }
}
