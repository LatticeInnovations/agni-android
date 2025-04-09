package com.latticeonfhir.core.data.server.model.labormed.labtest

import androidx.annotation.Keep

@Keep
data class LabTestRequest(
    val diagnosticUuid: String,
    val appointmentId: String,
    val patientId: String,
    val createdOn: String,
    val files: List<LabTestFile>
)