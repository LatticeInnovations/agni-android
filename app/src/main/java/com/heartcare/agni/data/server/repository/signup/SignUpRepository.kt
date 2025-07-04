package com.heartcare.agni.data.server.repository.signup

import com.heartcare.agni.data.server.enums.RegisterTypeEnum
import com.heartcare.agni.data.server.model.authentication.TokenResponse
import com.heartcare.agni.data.server.model.register.Register
import com.heartcare.agni.utils.converters.server.responsemapper.ResponseMapper

interface SignUpRepository {

    suspend fun verification(userContact: String, type: RegisterTypeEnum): ResponseMapper<String?>
    suspend fun otpVerification(
        userContact: String,
        otp: Int,
        type: RegisterTypeEnum
    ): ResponseMapper<TokenResponse>

    suspend fun register(register: Register, tempAuthToken: String): ResponseMapper<TokenResponse>
}