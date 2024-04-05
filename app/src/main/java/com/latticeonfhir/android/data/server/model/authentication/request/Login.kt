package com.latticeonfhir.android.data.server.model.authentication.request

import androidx.annotation.Keep

@Keep
data class Login(
    val isMobile: Boolean = true,
    val isdCode: String,
    val mobileNumber: String,
    val forgotPass: Boolean = false
)
