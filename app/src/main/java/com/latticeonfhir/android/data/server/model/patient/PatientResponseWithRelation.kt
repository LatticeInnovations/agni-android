package com.latticeonfhir.android.data.server.model.patient

import com.latticeonfhir.android.data.local.enums.RelationEnum

data class PatientResponseWithRelation(
    val patientResponse: PatientResponse,
    val relation: RelationEnum
)
