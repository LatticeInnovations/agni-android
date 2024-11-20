package com.latticeonfhir.android.data.local.enums

enum class YesNoEnum(val display: String, val code: Int) {
    YES("Yes", 1),
    NO("No", 0);

    companion object {
        fun listOfDisplay() = entries.map { it.display }
    }
}