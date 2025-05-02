package com.latticeonfhir.core.auth.data.server.model.register

import androidx.annotation.Keep

@Keep
data class Register(
    val firstName: String,
    val mobile: String?,
    val email: String?,
    val clinicName: String
)
