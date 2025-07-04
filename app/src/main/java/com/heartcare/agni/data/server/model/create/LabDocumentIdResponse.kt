package com.heartcare.agni.data.server.model.create

import androidx.annotation.Keep

@Keep
data class LabDocumentIdResponse(
    val labDocumentfhirId: String,
    val labDocumentUuid: String
)
