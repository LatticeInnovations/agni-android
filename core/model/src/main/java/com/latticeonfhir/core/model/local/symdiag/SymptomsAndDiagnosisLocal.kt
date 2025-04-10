package com.latticeonfhir.core.data.local.model.symdiag

import androidx.annotation.Keep
import java.util.Date

@Keep
data class SymptomsAndDiagnosisData(
    val patientId: String?,
    val appointmentId: String,
    val symDiagUuid: String,
    val createdOn: Date,
    val diagnosis: List<String>,
    val symptoms: List<String>,
)