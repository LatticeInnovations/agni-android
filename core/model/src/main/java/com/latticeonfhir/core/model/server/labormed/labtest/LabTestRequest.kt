package com.latticeonfhir.core.model.server.labormed.labtest

import androidx.annotation.Keep

@Keep
data class LabTestRequest(
    val diagnosticUuid: String,
    val appointmentId: String,
    val patientId: String,
    val createdOn: String,
    val files: List<LabTestFile>
)