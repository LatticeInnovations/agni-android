package com.latticeonfhir.core.model.server.authentication

import androidx.annotation.Keep

@Keep
data class TokenResponse(
    val token: String
)
