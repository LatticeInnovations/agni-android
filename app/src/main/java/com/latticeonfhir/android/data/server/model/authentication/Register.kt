package com.latticeonfhir.android.data.server.model.authentication

data class Register(
    val firstName: String,
    val lastName: String,
    val mobile: String?,
    val email: String?,
    val clinicName: String
)
