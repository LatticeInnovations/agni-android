package com.latticeonfhir.android.data.server.repository.authentication

import com.latticeonfhir.android.data.server.model.authentication.response.OtpResponse
import com.latticeonfhir.android.data.server.model.authentication.response.LoginResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ResponseMapper

interface AuthenticationRepository {

    suspend fun login(userContact: String): ResponseMapper<LoginResponse>
    suspend fun validateOtp(userContact: String, otp: String): ResponseMapper<OtpResponse>
}