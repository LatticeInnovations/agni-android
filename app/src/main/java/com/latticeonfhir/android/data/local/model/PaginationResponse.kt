package com.latticeonfhir.android.data.local.model

import androidx.annotation.Keep

@Keep
data class PaginationResponse<T>(
    val data: T,
    val size: Int
)
