package com.heartcare.agni.data.server.model.authentication

import androidx.annotation.Keep
import com.heartcare.agni.data.server.enums.RegisterTypeEnum

@Keep
data class Otp(
    val userContact: String,
    val otp: Int,
    val type: RegisterTypeEnum? = null
)
