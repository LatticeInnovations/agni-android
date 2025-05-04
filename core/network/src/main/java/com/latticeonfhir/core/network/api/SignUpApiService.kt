package com.latticeonfhir.core.network.api

import com.latticeonfhir.core.model.base.BaseResponse
import com.latticeonfhir.core.model.server.authentication.Login
import com.latticeonfhir.core.model.server.authentication.Otp
import com.latticeonfhir.core.model.server.authentication.TokenResponse
import com.latticeonfhir.core.model.server.register.Register
import com.latticeonfhir.core.network.constants.AuthenticationConstants.X_ACCESS_TOKEN
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface SignUpApiService {

    @POST("auth/verification")
    suspend fun verification(@Body login: Login): Response<BaseResponse<String?>>

    @POST("auth/verification/otp")
    suspend fun verificationOtp(@Body otp: Otp): Response<BaseResponse<TokenResponse>>

    @POST("user")
    suspend fun register(
        @Header(X_ACCESS_TOKEN) tempAuthToken: String,
        @Body register: Register
    ): Response<BaseResponse<TokenResponse>>
}