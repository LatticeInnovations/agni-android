package com.latticeonfhir.android.utils.converters.responseconverter

import com.latticeonfhir.android.data.server.model.patient.PatientAddressResponse
import org.hl7.fhir.r4.model.Address

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

    internal fun getAddressFhir(addressResponse: Address): String {
        return addressResponse.line[0].value +
                if (addressResponse.line.size<1) "" else {
                    ", " + addressResponse.line[1].value
                } + ", " + addressResponse.city +
                if (addressResponse.district.isNullOrEmpty()) "" else {
                    ", " + addressResponse.district
                } + ", " + addressResponse.state + ", " + addressResponse.postalCode
    }
}