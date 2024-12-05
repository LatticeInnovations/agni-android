package com.latticeonfhir.android.data.server.model.labormed.labtest

import androidx.annotation.Keep
import com.latticeonfhir.android.data.server.model.prescription.photo.File

@Keep
data class LabTestRequest(
    val appointmentId: String,
    val createdOn: String,
    val diagnosticUuid: String,
    val files: List<File>,
    val patientId: String
)