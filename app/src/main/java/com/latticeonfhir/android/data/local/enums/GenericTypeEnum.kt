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
    VITAL(11, "VitalPatch"),
    PRESCRIPTION_PHOTO_RESPONSE(12, "prescription_photo_response"),
    FHIR_IDS_PRESCRIPTION_PHOTO(13, "FHIR_IDS_PRESCRIPTION_PHOTO"),
    SYMPTOMS_DIAGNOSIS(14, "SymptomsAndDiagnosis"),
    PHOTO_DOWNLOAD(15, "photo_download"),
    LAB_TEST(16, "Lab_Test"),
    MEDICAL_RECORD(17, "Medical_Record"),
    DISPENSE(18, "dispense"),
    FHIR_IDS_DISPENSE(19, "FHIR_IDS_DISPENSE"),
    FHIR_IDS_OTC(20, "FHIR_IDS_OTC"),
    IMMUNIZATION(21, "Immunization");

    companion object {
        fun fromString(value: String) = entries.firstOrNull { it.value == value }
    }
}