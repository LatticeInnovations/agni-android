package com.latticeonfhir.android.utils.converters.responseconverter

import com.latticeonfhir.android.data.server.model.patient.PatientAddressResponse

object AddressConverter {
    internal fun getAddress(addressResponse: PatientAddressResponse): String {
        return addressResponse.addressLine1 +
                if (addressResponse.addressLine2.isNullOrEmpty()) "" else {
                    ", " + addressResponse.addressLine2
                } + ", " + addressResponse.city +
                if (addressResponse.district.isNullOrEmpty()) "" else {
                    ", " + addressResponse.district
                } + ", " + addressResponse.state + ", " + addressResponse.postalCode
    }
}