package com.latticeonfhir.android.data.server.model

data class CreateResponse(
    val status: String,
    val fhirId: String?,
    val id: String?,
    val error: String?
)
