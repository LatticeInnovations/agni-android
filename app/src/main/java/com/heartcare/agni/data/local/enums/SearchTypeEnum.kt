package com.heartcare.agni.data.local.enums

enum class SearchTypeEnum(val value: String) {

    PATIENT("Patient"),
    ACTIVE_INGREDIENT("Active Ingredient"),
    SYMPTOM("Symptom"),
    DIAGNOSIS("Diagnosis");

    companion object {
        fun fromString(value: String) = values().first { it.value == value }
    }
}