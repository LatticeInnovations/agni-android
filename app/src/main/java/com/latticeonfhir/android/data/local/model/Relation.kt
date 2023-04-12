package com.latticeonfhir.android.data.local.model

import androidx.annotation.Keep

@Keep
data class Relation(
    val patientId: String,
    val relativeId: String,
    val relation: String
)
