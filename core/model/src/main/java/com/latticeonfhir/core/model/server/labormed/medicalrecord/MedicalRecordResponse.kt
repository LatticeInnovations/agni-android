package com.latticeonfhir.core.model.server.labormed.medicalrecord

import androidx.annotation.Keep

@Keep
data class MedicalRecordResponse(
    val appointmentId: String,
    val appointmentUuid: String,
    val medicalRecord: List<MedicalRecord>,
    val patientId: String,
    val prescriptionFhirId: String
)