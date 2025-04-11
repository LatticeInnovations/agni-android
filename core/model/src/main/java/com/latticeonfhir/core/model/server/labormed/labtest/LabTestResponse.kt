package com.latticeonfhir.core.model.server.labormed.labtest

import androidx.annotation.Keep

@Keep
data class LabTestResponse(
    val appointmentId: String,
    val appointmentUuid: String,
    val diagnosticReport: List<DiagnosticReport>,
    val patientId: String,
    val prescriptionFhirId: String
)