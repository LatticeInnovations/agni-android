package com.latticeonfhir.android.data.server.model.labormed.medicalrecord

import androidx.annotation.Keep
import com.latticeonfhir.android.data.server.model.prescription.photo.File

@Keep
data class MedicalRecordRequest(
    val appointmentId: String,
    val createdOn: String,
    val files: List<File>,
    val medicalReportUuid: String,
    val patientId: String
)