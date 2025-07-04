package com.heartcare.agni.data.server.model.file.request

import androidx.annotation.Keep

@Keep
data class FilesRequest(
    val files: List<String>
)