package com.latticeonfhir.features.auth.data.server.model.authentication

import androidx.annotation.Keep

@Keep
data class TokenResponse(
    val token: String
)
