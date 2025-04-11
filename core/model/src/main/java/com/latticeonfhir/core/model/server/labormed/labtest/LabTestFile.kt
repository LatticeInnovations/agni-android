package com.latticeonfhir.core.model.server.labormed.labtest

import androidx.annotation.Keep

@Keep
data class LabTestFile(
    val labDocumentUuid: String,
    val filename: String,
    val note: String
)
