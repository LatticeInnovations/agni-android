package com.latticeonfhir.core.model.enums

enum class ChangeTypeEnum(val value: String) {
    ADD("add"),
    REMOVE("remove"),
    REPLACE("replace");

    companion object {
        fun fromString(value: String) = values().first { it.value == value }
    }
}