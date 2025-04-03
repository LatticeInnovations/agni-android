package com.latticeonfhir.core.model.enums

enum class PhotoUploadTypeEnum(val value: String) {
    LAB_TEST("lab_test"),
    MEDICAL_RECORD("medical_record");

    companion object {
        fun fromString(value: String) = ChangeTypeEnum.entries.first { it.value == value }
    }
}