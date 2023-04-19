package com.latticeonfhir.android.data.local.model

data class PaginationResponse<T>(
    val data: T,
    val size: Int
)
