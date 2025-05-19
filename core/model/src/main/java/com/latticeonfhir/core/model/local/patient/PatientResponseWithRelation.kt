package com.latticeonfhir.core.model.local.patient

import androidx.annotation.Keep
import com.latticeonfhir.core.model.enums.RelationEnum
import com.latticeonfhir.core.model.server.patient.PatientResponse

@Keep
data class PatientResponseWithRelation(
    val patientResponse: PatientResponse,
    val relation: RelationEnum
)
