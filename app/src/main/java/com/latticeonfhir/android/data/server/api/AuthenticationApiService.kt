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

    // https://fhir.api.thelattice.org/fhir/api/v1/
    @POST("/fhir/api/v1/auth/login")
    suspend fun login(@Body login: Login): Response<BaseResponse<String?>>

    @POST("/fhir/api/v1/auth/otp")
    suspend fun validateOtp(@Body otp: Otp): Response<BaseResponse<TokenResponse>>

    @GET("/fhir/api/v1/user")
    suspend fun getUserDetails(): Response<BaseResponse<UserResponse>>
}