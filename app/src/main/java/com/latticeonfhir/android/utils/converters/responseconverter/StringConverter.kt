package com.latticeonfhir.android.utils.converters.responseconverter

object StringConverter {
    fun String.formatToAbhaId(): String {
        val digits = this.filter { it.isDigit() }

        require(digits.length == 14) {
            "Input must contain exactly 14 digits"
        }

        return buildString {
            append(digits.take(2))
            append("-")
            append(digits.substring(2, 6))
            append("-")
            append(digits.substring(6, 10))
            append("-")
            append(digits.substring(10, 14))
        }
    }
}