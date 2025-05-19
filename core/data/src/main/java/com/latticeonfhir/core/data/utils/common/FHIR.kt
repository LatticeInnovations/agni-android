package com.latticeonfhir.core.data.utils.common

import com.latticeonfhir.core.utils.regex.OnlyNumberRegex

object FHIR {

    internal fun String.isFhirId(): Boolean {
        return this.matches(OnlyNumberRegex.onlyNumbers)
    }
}