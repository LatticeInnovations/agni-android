package com.latticeonfhir.core.auth.data.server.api

import com.latticeonfhir.android.auth.data.server.model.BaseResponse
import com.latticeonfhir.core.auth.data.server.model.authentication.Login
import com.latticeonfhir.core.auth.data.server.model.authentication.Otp
import com.latticeonfhir.core.auth.data.server.model.authentication.TokenResponse
import com.latticeonfhir.core.auth.data.server.model.user.UserResponse
import com.latticeonfhir.android.auth.utils.contants.AuthenticationConstants.TEMP_TOKEN
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