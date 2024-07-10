package com.latticeonfhir.android.data.server.repository.signup

import com.latticeonfhir.android.data.server.enums.RegisterTypeEnum
import com.latticeonfhir.android.data.server.model.register.Register
import com.latticeonfhir.android.data.server.model.authentication.TokenResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ResponseMapper

interface SignUpRepository {

    suspend fun verification(userContact: String, type: RegisterTypeEnum): ResponseMapper<String?>
    suspend fun otpVerification(userContact: String, otp: Int, type: RegisterTypeEnum): ResponseMapper<TokenResponse>
    suspend fun register(register: Register): ResponseMapper<TokenResponse>
}