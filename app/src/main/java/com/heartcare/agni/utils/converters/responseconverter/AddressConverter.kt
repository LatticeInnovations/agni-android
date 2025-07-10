package com.heartcare.agni.utils.converters.responseconverter

import com.heartcare.agni.data.server.model.patient.PatientAddressResponse

object AddressConverter {
    internal fun getAddress(addressResponse: PatientAddressResponse): String {
        return addressResponse.village +
                if (addressResponse.addressLine2.isNullOrEmpty()) "" else {
                    ", " + addressResponse.addressLine2
                } + ", " + addressResponse.areaCouncil +
                if (addressResponse.island.isEmpty()) "" else {
                    ", " + addressResponse.island
                } + ", " + addressResponse.province + ", " + addressResponse.postalCode
    }
}