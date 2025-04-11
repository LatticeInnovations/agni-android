package com.latticeonfhir.core.model.server.create

import androidx.annotation.Keep

@Keep
data class LabDocumentIdResponse(
    val labDocumentfhirId: String,
    val labDocumentUuid: String
)
