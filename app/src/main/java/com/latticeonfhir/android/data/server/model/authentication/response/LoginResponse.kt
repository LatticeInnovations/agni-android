package com.latticeonfhir.android.data.server.model.authentication.response

import androidx.annotation.Keep

@Keep
data class LoginResponse(
    val otpPage: Boolean
)