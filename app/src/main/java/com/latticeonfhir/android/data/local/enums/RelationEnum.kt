package com.latticeonfhir.android.data.local.enums

enum class RelationEnum(val number: Int, val value: String) {
    MOTHER(0,"MTH"),
    SON(1,"SON"),
    BROTHER(2,"BRO"),
    SISTER(3,"SIS");

    companion object {
        fun fromInt(number: Int) = values().first { it.number == number }
        fun fromString(value: String) = values().first { it.value == value }
    }
}