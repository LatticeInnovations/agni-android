package com.latticeonfhir.android.data.local.model

import com.google.gson.annotations.Expose

data class ChangeRequest(
    @Expose(deserialize = false)
    val key: String? = null,
    val operation: String,
    val value: Any
)
