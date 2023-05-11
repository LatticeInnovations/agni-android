package com.latticeonfhir.android.data.server.model.authentication

import com.google.errorprone.annotations.Keep

@Keep
data class Otp(
    val userContact: String,
    val otp: Int
)
