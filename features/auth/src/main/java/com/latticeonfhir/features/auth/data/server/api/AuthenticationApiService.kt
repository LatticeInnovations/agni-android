package com.latticeonfhir.features.auth.data.server.api

import com.latticeonfhir.features.auth.data.server.model.authentication.Login
import com.latticeonfhir.features.auth.data.server.model.authentication.Otp
import com.latticeonfhir.features.auth.data.server.model.authentication.TokenResponse
import com.latticeonfhir.features.auth.data.server.model.user.UserResponse
import com.latticeonfhir.features.auth.utils.contants.AuthenticationConstants.TEMP_TOKEN
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthenticationApiService {

    @POST("auth/login")
    suspend fun login(@Body login: Login): Response<com.latticeonfhir.features.auth.data.server.model.BaseResponse<String?>>

    @POST("auth/otp")
    suspend fun validateOtp(@Body otp: Otp): Response<com.latticeonfhir.features.auth.data.server.model.BaseResponse<TokenResponse>>

    @GET("user")
    suspend fun getUserDetails(): Response<com.latticeonfhir.features.auth.data.server.model.BaseResponse<UserResponse>>

    @DELETE("user")
    suspend fun deleteUserDetails(@Header(TEMP_TOKEN) tempToken: String): Response<com.latticeonfhir.features.auth.data.server.model.BaseResponse<String?>>
}