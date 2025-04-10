package com.latticeonfhir.core.base.server

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
