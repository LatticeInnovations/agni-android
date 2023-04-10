package com.latticeonfhir.android.data.local.model

import androidx.annotation.Keep
import com.google.gson.annotations.Expose

@Keep
data class ChangeRequest(
    @Expose(deserialize = false)
    val key: String? = null,
    val operation: String,
    val value: Any
)
