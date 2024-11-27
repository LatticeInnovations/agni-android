package com.latticeonfhir.android.data.server.model.labormed

import androidx.annotation.Keep

@Keep
data class Document(
    val filename: String,
    val note: String,
    val url: String
)