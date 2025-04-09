package com.latticeonfhir.core.data.local.enums

enum class MedFrequencyEnum(val number: Int, val value: String) {
    OD(1, "OD"),
    BD(2, "BD"),
    TDS(3, "TDS"),
    QID(4, "QID");

    companion object {
        fun fromInt(number: Int) = MedFrequencyEnum.values().first { it.number == number }
        fun fromString(value: String) = MedFrequencyEnum.values().first { it.value == value }
    }
}