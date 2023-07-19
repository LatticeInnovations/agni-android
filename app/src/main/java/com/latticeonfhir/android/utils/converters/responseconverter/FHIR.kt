package com.latticeonfhir.android.utils.converters.responseconverter

import com.latticeonfhir.android.utils.regex.OnlyNumberRegex.onlyNumbers

object FHIR {

    internal fun String.isFhirId(): Boolean {
        return this.matches(onlyNumbers)
    }
}