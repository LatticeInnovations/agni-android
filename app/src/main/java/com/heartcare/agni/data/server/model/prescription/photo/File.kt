package com.heartcare.agni.data.server.model.prescription.photo

import androidx.annotation.Keep

@Keep
data class File(
    val documentUuid: String,
    val documentFhirId: String?,
    val filename: String,
    val note: String
)
