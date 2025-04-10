package com.latticeonfhir.core.data.local.model.patient

import androidx.annotation.Keep
import com.latticeonfhir.core.data.local.enums.RelationEnum
import com.latticeonfhir.core.data.server.model.patient.PatientResponse

@Keep
data class PatientResponseWithRelation(
    val patientResponse: PatientResponse,
    val relation: RelationEnum
)
