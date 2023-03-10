package com.latticeonfhir.android.data.local.model

data class ChangeRequest(
    val operation: String,
    val value: Any
)
