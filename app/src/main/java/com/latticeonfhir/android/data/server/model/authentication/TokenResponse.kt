package com.latticeonfhir.android.data.server.model.authentication

import androidx.annotation.Keep

@Keep
data class TokenResponse(
    val contact: String,
    val name: String,
    val orgId: String,
    val role: String,
    val roleName: String,
    val token: String,
    val userId: String
)