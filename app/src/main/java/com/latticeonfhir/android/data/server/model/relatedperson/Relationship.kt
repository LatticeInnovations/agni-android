package com.latticeonfhir.android.data.server.model.relatedperson

data class Relationship(
    val patientIs: String,
    val relativeId: String,
    val relativeIs: String
)