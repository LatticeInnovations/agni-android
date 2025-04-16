package com.latticeonfhir.core.model.server.labormed.medicalrecord

import androidx.annotation.Keep

@Keep
data class MedRecordFile(
    val medicalDocumentUuid: String,
    val filename: String,
    val note: String
)