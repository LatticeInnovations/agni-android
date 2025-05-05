package com.latticeonfhir.core.data.repository.server.signup

import com.latticeonfhir.core.model.enums.RegisterTypeEnum
import com.latticeonfhir.core.model.server.authentication.TokenResponse
import com.latticeonfhir.core.model.server.register.Register
import com.latticeonfhir.core.utils.converters.responsemapper.ResponseMapper

interface SignUpRepository {

    suspend fun verification(userContact: String, type: RegisterTypeEnum): ResponseMapper<String?>
    suspend fun otpVerification(
        userContact: String,
        otp: Int,
        type: RegisterTypeEnum
    ): ResponseMapper<TokenResponse>

    suspend fun register(register: Register, tempAuthToken: String): ResponseMapper<TokenResponse>
}