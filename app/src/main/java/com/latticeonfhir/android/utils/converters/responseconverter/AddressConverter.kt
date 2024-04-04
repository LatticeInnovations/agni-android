package com.latticeonfhir.android.utils.converters.responseconverter

import org.hl7.fhir.r4.model.Address

object AddressConverter {

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