package com.latticeonfhir.android.data.server.model.patient

import androidx.annotation.Keep
import com.latticeonfhir.android.data.local.enums.RelationEnum

@Keep
data class PatientResponseWithRelation(
    val patientResponse: PatientResponse,
    val relation: RelationEnum
)
