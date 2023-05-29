package com.latticeonfhir.android.data.local.model.patient

import androidx.annotation.Keep
import com.latticeonfhir.android.data.local.enums.RelationEnum
import com.latticeonfhir.android.data.server.model.patient.PatientResponse

@Keep
data class PatientResponseWithRelation(
    val patientResponse: PatientResponse,
    val relation: RelationEnum
)
