package com.latticeonfhir.android.base.server

import com.google.gson.annotations.SerializedName

data class BaseResponse<T>(
    val status: Int,
    val message: String,
    val total: Int,
    val offset: Int?,
    val data: T?,
    val error: String?
)
