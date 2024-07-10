package com.latticeonfhir.android.data.server.model.prescription.photo

import androidx.annotation.Keep

@Keep
data class File(
    val filename: String,
    val note: String
)