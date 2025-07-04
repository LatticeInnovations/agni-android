package com.heartcare.agni.data.local.model.patient

import androidx.annotation.Keep
import com.heartcare.agni.data.local.enums.RelationEnum
import com.heartcare.agni.data.server.model.patient.PatientResponse

@Keep
data class PatientResponseWithRelation(
    val patientResponse: PatientResponse,
    val relation: RelationEnum
)
