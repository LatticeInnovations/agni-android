package com.latticeonfhir.android.data.local.enums

enum class GenderEnum(val number: Int, val value: String) {
    MALE(0, "male"),
    FEMALE(1, "female"),
    OTHER(2, "other"),
    UNKNOWN(3, "unknown");

    companion object {
        fun fromString(value: String) = entries.first { it.value == value }
    }
}