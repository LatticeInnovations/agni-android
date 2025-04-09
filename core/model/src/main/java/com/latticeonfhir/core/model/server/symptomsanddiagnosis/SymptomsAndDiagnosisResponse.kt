package com.latticeonfhir.core.data.server.model.symptomsanddiagnosis

import androidx.annotation.Keep

@Keep
data class SymptomsAndDiagnosisResponse(
    val appointmentId: String,
    val patientId: String,
    val createdOn: String,
    val diagnosis: List<SymptomsAndDiagnosisItem>,
    val practitionerName: String,
    val symDiagFhirId: String,
    val symDiagUuid: String,
    val symptoms: List<SymptomsAndDiagnosisItem>
)
