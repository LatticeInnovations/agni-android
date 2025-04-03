package com.latticeonfhir.core.model.enums

enum class YesNoEnum(val display: String, val code: Int) {
    YES("Yes", 1),
    NO("No", 0);

    companion object {
        fun listOfDisplay() = entries.map { it.display }
        fun codeFromDisplay(display: String) = entries.first { it.display == display }.code
        fun displayFromCode(code: Int) = entries.first { it.code == code }.display
    }
}