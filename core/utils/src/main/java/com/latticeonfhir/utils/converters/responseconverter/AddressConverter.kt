package com.latticeonfhir.utils.converters.responseconverter

import com.latticeonfhir.core.data.server.model.patient.PatientAddressResponse

object AddressConverter {
    fun getAddress(addressResponse: PatientAddressResponse): String {
        return addressResponse.addressLine1 +
                if (addressResponse.addressLine2.isNullOrEmpty()) "" else {
                    ", " + addressResponse.addressLine2
                } + ", " + addressResponse.city +
                if (addressResponse.district.isNullOrEmpty()) "" else {
                    ", " + addressResponse.district
                } + ", " + addressResponse.state + ", " + addressResponse.postalCode
    }
}