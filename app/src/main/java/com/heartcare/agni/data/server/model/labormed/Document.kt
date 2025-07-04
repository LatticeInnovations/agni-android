package com.heartcare.agni.data.server.model.labormed

import androidx.annotation.Keep

@Keep
data class Document(
    val filename: String,
    val note: String,
    val labDocumentfhirId: String,
    val labDocumentUuid: String
)

@Keep
data class MedDocument(
    val filename: String,
    val note: String,
    val medicalDocumentfhirId: String,
    val medicalDocumentUuid: String
)