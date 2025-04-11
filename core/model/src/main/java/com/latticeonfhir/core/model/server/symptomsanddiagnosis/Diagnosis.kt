package com.latticeonfhir.core.model.server.symptomsanddiagnosis

import androidx.annotation.Keep

@Keep
data class Diagnosis(
    val diagnosis: List<SymptomsAndDiagnosisItem>
)