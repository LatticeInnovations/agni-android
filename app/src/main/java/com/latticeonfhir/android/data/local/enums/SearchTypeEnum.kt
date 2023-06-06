package com.latticeonfhir.android.data.local.enums

enum class SearchTypeEnum(val value: String) {

    PATIENT("Patient"),
    ACTIVE_INGREDIENT("Active Ingredient");

    companion object {
        fun fromString(value: String) = values().first { it.value == value }
    }
}