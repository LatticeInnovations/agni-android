package com.latticeonfhir.android.data.server.model.authentication.request

import androidx.annotation.Keep

@Keep
data class Otp(
    val isMobile: Boolean=true,
    val isdCode: String,
    val mobileNumber: String,
    val otp: String
)
