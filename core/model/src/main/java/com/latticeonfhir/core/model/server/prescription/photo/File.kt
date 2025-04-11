package com.latticeonfhir.core.model.server.prescription.photo

import androidx.annotation.Keep

@Keep
data class File(
    val documentUuid: String,
    val documentFhirId: String?,
    val filename: String,
    val note: String
)
