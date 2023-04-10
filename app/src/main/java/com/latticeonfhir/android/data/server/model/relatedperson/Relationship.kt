package com.latticeonfhir.android.data.server.model.relatedperson

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class Relationship(
    @SerializedName("patientIs")
    val patientId: String,
    val relativeId: String,
    @SerializedName("relativeIs")
    val relation: String
)