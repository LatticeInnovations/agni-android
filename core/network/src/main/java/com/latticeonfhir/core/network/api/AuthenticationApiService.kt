package com.latticeonfhir.core.network.api

import com.latticeonfhir.core.model.base.BaseResponse
import com.latticeonfhir.core.model.server.authentication.Login
import com.latticeonfhir.core.model.server.authentication.Otp
import com.latticeonfhir.core.model.server.authentication.TokenResponse
import com.latticeonfhir.core.model.server.user.UserResponse
import com.latticeonfhir.core.network.constants.AuthenticationConstants.TEMP_TOKEN
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthenticationApiService {

    @POST("auth/login")
    suspend fun login(@Body login: Login): Response<BaseResponse<String?>>

    @POST("auth/otp")
    suspend fun validateOtp(@Body otp: Otp): Response<BaseResponse<TokenResponse>>

    @GET("user")
    suspend fun getUserDetails(): Response<BaseResponse<UserResponse>>

    @DELETE("user")
    suspend fun deleteUserDetails(@Header(TEMP_TOKEN) tempToken: String): Response<BaseResponse<String?>>
}