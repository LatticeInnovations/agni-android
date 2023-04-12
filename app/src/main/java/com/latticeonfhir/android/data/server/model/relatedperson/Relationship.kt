package com.latticeonfhir.android.data.server.model.relatedperson

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class Relationship(
    val patientIs: String,
    val relativeId: String,
    val relativeIs: String
)