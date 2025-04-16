package com.latticeonfhir.core.model.server.file.request

import androidx.annotation.Keep

@Keep
data class FilesRequest(
    val files: List<String>
)