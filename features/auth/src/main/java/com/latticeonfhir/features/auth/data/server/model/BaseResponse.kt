package com.latticeonfhir.features.auth.data.server.model

import androidx.annotation.Keep

@Keep
data class BaseResponse<T>(
    val status: Int,
    val message: String,
    val total: Int?,
    val offset: Int?,
    val data: T?,
    val error: String?
)
