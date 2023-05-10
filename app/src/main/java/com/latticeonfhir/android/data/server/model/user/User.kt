package com.latticeonfhir.android.data.server.model.user

import com.google.errorprone.annotations.Keep

@Keep
data class User(
    val userName: String,
    val role: String,
    val mobileNumber: Long?,
    val email: String?
)
