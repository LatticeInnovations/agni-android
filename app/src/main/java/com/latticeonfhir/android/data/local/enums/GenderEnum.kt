package com.latticeonfhir.android.data.local.enums

enum class GenderEnum(val number: Int, val value: String) {
    MALE(0,"male"),
    FEMALE(1,"female"),
    OTHER(2,"other"),
    UNKNOWN(3,"unknown");

    companion object {
        fun fromInt(number: Int) = values().first { it.number == number }
        fun fromString(value: String) = values().first { it.value == value }
    }
}