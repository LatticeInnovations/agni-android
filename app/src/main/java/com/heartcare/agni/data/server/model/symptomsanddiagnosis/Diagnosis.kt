package com.heartcare.agni.data.server.model.symptomsanddiagnosis

import androidx.annotation.Keep

@Keep
data class Diagnosis(
    val diagnosis: List<SymptomsAndDiagnosisItem>
)