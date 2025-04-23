package com.latticeonfhir.core.utils.converters.responseconverter

import com.latticeonfhir.core.utils.regex.OnlyNumberRegex.onlyNumbers

object FHIR {

    internal fun String.isFhirId(): Boolean {
        return this.matches(onlyNumbers)
    }
}