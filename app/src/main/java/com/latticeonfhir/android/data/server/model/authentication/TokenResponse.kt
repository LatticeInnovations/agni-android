package com.latticeonfhir.android.data.server.model.authentication

import com.google.errorprone.annotations.Keep

@Keep
data class TokenResponse(
    val token: String
)
