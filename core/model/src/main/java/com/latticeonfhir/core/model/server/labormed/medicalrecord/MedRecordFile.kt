package com.latticeonfhir.core.data.server.model.labormed.medicalrecord

import androidx.annotation.Keep

@Keep
data class MedRecordFile(
    val medicalDocumentUuid: String,
    val filename: String,
    val note: String
)