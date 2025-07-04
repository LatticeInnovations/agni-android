package com.heartcare.agni.data.server.model.user

import androidx.annotation.Keep

@Keep
data class UserResponse(
    val userId: String,
    val userName: String,
    val role: List<UserRoleDetails>,
    val mobileNumber: Long?,
    val userEmail: String?
)
