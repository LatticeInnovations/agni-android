package com.latticeonfhir.android.data.local.model.pagination

import androidx.annotation.Keep

@Keep
data class PaginationResponse<T>(
    val data: T,
    val size: Int
)
