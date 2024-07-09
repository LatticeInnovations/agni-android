package com.latticeonfhir.android.data.server.api

import com.latticeonfhir.android.base.server.BaseResponse
import com.latticeonfhir.android.data.server.model.authentication.Login
import com.latticeonfhir.android.data.server.model.authentication.Otp
import com.latticeonfhir.android.data.server.model.authentication.Register
import com.latticeonfhir.android.data.server.model.authentication.TokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface SignUpApiService {

    @POST("auth/verification")
    suspend fun verification(@Body login: Login): Response<BaseResponse<String?>>

    @POST("auth/verification/otp")
    suspend fun verificationOtp(@Body otp: Otp): Response<BaseResponse<TokenResponse>>

    @POST("/register")
    suspend fun register(@Body register: Register): Response<BaseResponse<TokenResponse>>

}