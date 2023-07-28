package com.latticeonfhir.android.data.local.enums

enum class GenericTypeEnum(val number: Int, val value: String) {
    PATIENT(1,"Patient"),
    RELATION(2,"Relation"),
    FHIR_IDS(3,"FHIR_IDS"),
    PRESCRIPTION(4,"Prescription"),
    APPOINTMENT(5,"Appointment"),
    SCHEDULE(6,"Schedule");

    companion object {
        fun fromInt(number: Int) = values().firstOrNull { it.number == number }
        fun fromString(value: String) = values().firstOrNull { it.value == value }
    }
}