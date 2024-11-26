package com.latticeonfhir.android.utils.regex

object OnlyNumberRegex {
    val onlyNumbers = Regex("^\\d+\$")
    val onlyNumbersWithDecimal = Regex("^([0-9]+\\.?|[0-9]*\\.[0-9])\$")
}