package com.latticeonfhir.android.data.local.enums

enum class GenericTypeEnum(val number: Int, val value: String) {
    PERSON(1,"Person"),
    PATIENT(2,"Patient"),
    MEDICAL_RECORD(3,"Medical Record");

    companion object {
        fun fromInt(number: Int) = values().firstOrNull { it.number == number }
        fun fromString(value: String) = values().firstOrNull { it.value == value }
    }
}