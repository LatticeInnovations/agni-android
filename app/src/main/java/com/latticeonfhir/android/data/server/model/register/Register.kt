package com.latticeonfhir.android.data.server.model.register

data class Register(
    val firstName: String,
    val mobile: String?,
    val email: String?,
    val clinicName: String
)
