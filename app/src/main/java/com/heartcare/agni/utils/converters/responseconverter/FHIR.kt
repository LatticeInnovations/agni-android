package com.heartcare.agni.utils.converters.responseconverter

import com.heartcare.agni.utils.regex.OnlyNumberRegex.onlyNumbers

object FHIR {

    internal fun String.isFhirId(): Boolean {
        return this.matches(onlyNumbers)
    }
}