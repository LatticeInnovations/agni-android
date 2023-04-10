package com.latticeonfhir.android.data.server.model.relatedperson

import com.google.gson.annotations.SerializedName

data class Relationship(
    @SerializedName("patientIs")
    val patientId: String,
    val relativeId: String,
    @SerializedName("relativeIs")
    val relation: String
)