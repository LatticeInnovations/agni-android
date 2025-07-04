package com.heartcare.agni.data.server.model.authentication

import androidx.annotation.Keep
import com.heartcare.agni.data.server.enums.RegisterTypeEnum

@Keep
data class Login(
    val userContact: String,
    val type: RegisterTypeEnum? = null
)
