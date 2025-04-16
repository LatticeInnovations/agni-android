package com.latticeonfhir.core.model.server.create

import androidx.annotation.Keep

@Keep
data class MedDocumentIdResponse(
    val medicalDocumentfhirId: String,
    val medicalDocumentUuid: String
)