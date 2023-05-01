package com.latticeonfhir.android.data.server.model.relatedperson

import androidx.annotation.Keep

@Keep
data class Relationship(
    val patientIs: String,
    val relativeId: String
)