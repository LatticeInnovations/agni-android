package com.latticeonfhir.android.data.local.constants

import com.latticeonfhir.android.data.server.model.patient.PatientAddressResponse
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTimeInMilli
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId

object Constants {

    internal fun GetFullName(firstName: String?, middleName: String?, lastName: String?): String{
        return firstName +
                if (middleName.isNullOrEmpty()) "" else {
                    " " + middleName
                } +
                if (lastName.isNullOrEmpty()) "" else {
                    " " + lastName
                }
    }

    internal fun GetAge(birthDate: String): Int {
        return Period.between(
            Instant.ofEpochMilli(birthDate.toTimeInMilli()).atZone(ZoneId.systemDefault())
                .toLocalDate(),
            LocalDate.now()
        ).years
    }

    internal fun GetAddress(addressResponse: PatientAddressResponse): String {
        return addressResponse.addressLine1 +
                if (addressResponse.addressLine2.isNullOrEmpty()) "" else {
                    ", "+ addressResponse.addressLine2
                } +", "+ addressResponse.city +
                if (addressResponse.district.isNullOrEmpty()) "" else {
                    ", "+ addressResponse.district
                } +", "+ addressResponse.state +", "+ addressResponse.postalCode
    }

    internal fun GetStateList() : List<String> {
        return listOf("Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar",
            "Chhattisgarh", "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jharkhand",
            "Karnataka", "Kerala", "Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya",
            "Mizoram", "Nagaland", "Odisha", "Punjab", "Rajasthan", "Sikkim", "Tamil Nadu",
            "Telangana", "Tripura", "Uttar Pradesh", "Uttarakhand", "West Bengal")
    }

    internal fun GetRelationshipList(gender: String): List<String>{
        if (gender == "male") return listOf(
            "Son",
            "Father",
            "Grand Father",
            "Brother",
            "Grand Son",
            "Uncle",
            "Brother-in-law",
            "Father-in-law",
            "Son-in-law",
            "Nephew",
            "Husband"
        )
        else if (gender == "female") return listOf(
            "Daughter",
            "Mother",
            "Grand Mother",
            "Sister",
            "Grand Daughter",
            "Aunty",
            "Sister-in-law",
            "Mother-in-law",
            "Daughter-in-law",
            "Niece",
            "Wife"
        )
        else return listOf(
            "Child",
            "Parent",
            "Grand Parent",
            "Sibling",
            "Grand Child",
            "In-Law",
            "Spouse"
        )
    }
}