package com.latticeonfhir.android.data.server.model.user

import com.google.errorprone.annotations.Keep

@Keep
data class UserResponse(
    val userName: String,
    val role: String,
    val mobileNumber: Long?,
    val userEmail: String?
)
