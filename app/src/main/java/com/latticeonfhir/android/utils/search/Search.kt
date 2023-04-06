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
        val finalList = totalList.toMutableList()
        searchParameters.run {
            if (!name.isNullOrBlank()) {
                finalList.filter {
                    FuzzySearch.ratio(name, it.patientEntity.firstName) > matchingRatio
                }
            }
            if (!patientId.isNullOrBlank()) {
                finalList.filter {
                    FuzzySearch.ratio(patientId, it.identifiers.find { identifier -> identifier.identifierNumber == patientId }?.identifierNumber) > matchingRatio
                }
            }
            if (minAge != null && maxAge != null) {
                finalList.filter {
                    (minAge <= it.patientEntity.birthDate.toAge()) && (it.patientEntity.birthDate <= maxAge)
                }
            }
            if (!lastFacilityVisit.isNullOrBlank()) {
                finalList.filter {
                    true
                }
            }
            if (!addressLine1.isNullOrBlank()) {
                finalList.filter {
                    FuzzySearch.ratio(
                        addressLine1,
                        it.patientEntity.permanentAddress.addressLine1
                    ) > matchingRatio
                }
            }
            if (!city.isNullOrBlank()) {
                finalList.filter {
                    FuzzySearch.ratio(
                        city,
                        it.patientEntity.permanentAddress.city
                    ) > matchingRatio
                }
            }
            if (!district.isNullOrBlank()) {
                finalList.filter {
                    FuzzySearch.ratio(
                        district,
                        it.patientEntity.permanentAddress.district
                    ) > matchingRatio
                }
            }
            if (!state.isNullOrBlank()) {
                finalList.filter {
                    FuzzySearch.ratio(
                        state,
                        it.patientEntity.permanentAddress.state
                    ) > matchingRatio
                }
            }
            if (!postalCode.isNullOrBlank()) {
                finalList.filter {
                    FuzzySearch.ratio(
                        postalCode,
                        it.patientEntity.permanentAddress.postalCode
                    ) > matchingRatio
                }
            }
            if (!country.isNullOrBlank()) {
                finalList.filter {
                    FuzzySearch.ratio(
                        country,
                        it.patientEntity.permanentAddress.country
                    ) > matchingRatio
                }
            }
            if (!addressLine2.isNullOrBlank()) {
                finalList.filter {
                    FuzzySearch.ratio(
                        addressLine2,
                        it.patientEntity.permanentAddress.addressLine2
                    ) > matchingRatio
                }
            }
        }
        return finalList
    }
}
