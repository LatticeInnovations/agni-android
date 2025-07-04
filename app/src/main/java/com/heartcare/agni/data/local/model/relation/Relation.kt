package com.heartcare.agni.data.local.model.relation

import androidx.annotation.Keep

@Keep
data class Relation(
    val patientId: String,
    val relativeId: String,
    val relation: String
)
