package com.latticeonfhir.android.data.server.model.labormed.medicalrecord

import androidx.annotation.Keep

@Keep
data class MedicalRecordRequest(
    val medicalReportUuid: String,
    val appointmentId: String,
    val patientId: String,
    val createdOn: String,
    val files: List<MedRecordFile>
)