package com.latticeonfhir.android.data.server.model.authentication

import androidx.annotation.Keep

@Keep
data class Otp(
    val userContact: String,
    val otp: Int
)
