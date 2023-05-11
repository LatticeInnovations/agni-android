package com.latticeonfhir.android.data.server.repository.authentication

import com.latticeonfhir.android.data.server.model.authentication.TokenResponse
import com.latticeonfhir.android.data.server.model.user.UserResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ResponseMapper

interface AuthenticationRepository {

    suspend fun login(userContact: String): ResponseMapper<String?>
    suspend fun validateOtp(userContact: String, otp: Int): ResponseMapper<TokenResponse>
    suspend fun getUserDetails(): ResponseMapper<UserResponse>
}