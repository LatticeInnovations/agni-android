package com.latticeonfhir.android.data.local.enums

enum class GenericTypeEnum(val number: Int, val value: String) {
    PATIENT(1,"Patient"),
    MEDICAL_RECORD(2,"Medical Record"),
    RELATION(3,"Relation"),
    FHIR_IDS(4,"FHIR_IDS");

    companion object {
        fun fromInt(number: Int) = values().firstOrNull { it.number == number }
        fun fromString(value: String) = values().firstOrNull { it.value == value }
    }
}