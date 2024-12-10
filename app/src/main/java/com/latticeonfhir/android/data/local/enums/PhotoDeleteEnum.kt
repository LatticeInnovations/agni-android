package com.latticeonfhir.android.data.local.enums


enum class PhotoDeleteEnum(val value: String) {
    DELETE("deleted"),
    SAVED("saved");

    companion object {
        fun fromString(value: String) = ChangeTypeEnum.entries.first { it.value == value }
    }
}