package com.latticeonfhir.android.data.local.enums

enum class DispenseStatusEnum(val display: String, val code: String) {
    NOT_DISPENSED("Not dispensed", "not-dispensed"),
    PARTIALLY_DISPENSED("Partially dispensed", "partially-dispensed"),
    FULLY_DISPENSED("Fully dispensed", "fully-dispensed");

    companion object {
        fun codeToDisplay(code: String) = run { entries.first { it.code == code }.display }
    }
}