package com.latticeonfhir.android.data.local.enums

enum class GenericTypeEnum(val number: Int, val value: String) {
    PATIENT(1, "Patient"),
    RELATION(2, "Relation"),
    FHIR_IDS(3, "FHIR_IDS"),
    PRESCRIPTION(4, "Prescription"),
    APPOINTMENT(5, "Appointment"),
    SCHEDULE(6, "Schedule"),
    LAST_UPDATED(7, "LAST_UPDATED"),
    FHIR_IDS_PRESCRIPTION(8, "FHIR_IDS_PRESCRIPTION"),
    PRESCRIPTION_PHOTO(9, "prescription_photo"),
    CVD(10, "cvd_record"),
    PHOTO_DOWNLOAD(11, "photo_download"),
    LAB_TEST(12, "Lab_Test"),
    MEDICAL_RECORD(13, "Medical_Record");

    companion object {
        fun fromString(value: String) = entries.firstOrNull { it.value == value }
    }
}