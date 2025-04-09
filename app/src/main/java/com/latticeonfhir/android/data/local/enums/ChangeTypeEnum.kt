package com.latticeonfhir.core.data.local.enums

enum class ChangeTypeEnum(val value: String) {
    ADD("add"),
    REMOVE("remove"),
    REPLACE("replace");

    companion object {
        fun fromString(value: String) = values().first { it.value == value }
    }
}