package com.latticeonfhir.android.data.server.api

import com.latticeonfhir.android.base.server.BaseResponse
import com.latticeonfhir.android.data.server.model.authentication.Login
import com.latticeonfhir.android.data.server.model.authentication.Otp
import com.latticeonfhir.android.data.server.model.authentication.TokenResponse
import com.latticeonfhir.android.data.server.model.user.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthenticationApiService {

    @POST("auth/login")
    suspend fun login(@Body login: Login): Response<BaseResponse<String?>>

    @POST("auth/otp")
    suspend fun validateOtp(@Body otp: Otp): Response<BaseResponse<TokenResponse>>

    @GET("user")
    suspend fun getUserDetails(): Response<BaseResponse<UserResponse>>
}