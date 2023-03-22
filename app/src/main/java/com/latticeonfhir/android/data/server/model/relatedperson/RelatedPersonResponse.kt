package com.latticeonfhir.android.data.server.model.relatedperson

data class RelatedPersonResponse(
    val id: String,
    val relationship: List<Relationship>
)