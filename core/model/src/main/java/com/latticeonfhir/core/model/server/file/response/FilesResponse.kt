package com.latticeonfhir.core.model.server.file.response

import androidx.annotation.Keep

@Keep
data class FilesResponse(
    val files: List<Files>,
    val errors: List<Files>
)

@Keep
data class Files(
    val originalName: String,
    val filename: String,
    val url: String?,
    val error: String?
)