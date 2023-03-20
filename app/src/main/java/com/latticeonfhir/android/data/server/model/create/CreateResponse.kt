package com.latticeonfhir.android.data.server.model.create

data class CreateResponse(
    val status: String,
    val fhirId: String?,
    val id: String?,
    val error: String?
)
