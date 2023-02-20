package com.latticeonfhir.android.data.server.model

data class PersonIdentifier(
    val identifierType: String,
    val identifierNumber: String?,
    val code: String
)
