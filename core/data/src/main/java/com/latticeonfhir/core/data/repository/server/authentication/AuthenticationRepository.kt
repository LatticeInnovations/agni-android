package com.latticeonfhir.core.data.repository.server.authentication

import com.latticeonfhir.core.utils.converters.responsemapper.ResponseMapper
import com.latticeonfhir.core.model.server.authentication.TokenResponse
import com.latticeonfhir.core.model.server.user.UserResponse

interface AuthenticationRepository {

    suspend fun login(userContact: String): ResponseMapper<String?>
    suspend fun validateOtp(userContact: String, otp: Int): ResponseMapper<TokenResponse>
    suspend fun getUserDetails(): ResponseMapper<UserResponse>
    suspend fun deleteAccount(tempToken: String): ResponseMapper<String?>
}