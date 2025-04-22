package com.latticeonfhir.android.auth.data.server.api

import com.latticeonfhir.core.auth.data.server.model.BaseResponse
import com.latticeonfhir.android.auth.data.server.model.authentication.Login
import com.latticeonfhir.android.auth.data.server.model.authentication.Otp
import com.latticeonfhir.core.auth.data.server.model.authentication.TokenResponse
import com.latticeonfhir.core.auth.data.server.model.register.Register
import com.latticeonfhir.android.auth.utils.contants.AuthenticationConstants.X_ACCESS_TOKEN
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