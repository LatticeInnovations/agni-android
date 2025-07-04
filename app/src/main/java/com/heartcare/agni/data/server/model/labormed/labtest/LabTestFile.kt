package com.heartcare.agni.data.server.model.labormed.labtest

import androidx.annotation.Keep

@Keep
data class LabTestFile(
    val labDocumentUuid: String,
    val filename: String,
    val note: String
)
