package com.latticeonfhir.android.data.server.model.symptomsanddiagnosis

import androidx.annotation.Keep
import com.latticeonfhir.android.data.server.model.symptomsanddiagnosis.SymptomsAndDiagnosisItem

@Keep
data class Diagnosis(
    val diagnosis: List<SymptomsAndDiagnosisItem>
)