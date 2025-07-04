package com.heartcare.agni.data.server.model.labormed.medicalrecord

import androidx.annotation.Keep

@Keep
data class MedicalRecordResponse(
    val appointmentId: String,
    val appointmentUuid: String,
    val medicalRecord: List<MedicalRecord>,
    val patientId: String,
    val prescriptionFhirId: String
)