package com.latticeonfhir.android.base.server

data class BaseResponse<T>(
    val status: Int,
    val message: String,
    val data: T?,
    val error: String?
)
