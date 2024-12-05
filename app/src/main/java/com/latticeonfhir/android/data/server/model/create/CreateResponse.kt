package com.latticeonfhir.android.data.server.model.create

import androidx.annotation.Keep

@Keep
data class CreateResponse(
    val status: String,
    val fhirId: String?,
    val id: String?,
    val error: String?,
    val prescription: List<MedReqIdResponse>?,
    val prescriptionFiles: List<DocumentIdResponse>?,
    val files: List<*>?,
)
