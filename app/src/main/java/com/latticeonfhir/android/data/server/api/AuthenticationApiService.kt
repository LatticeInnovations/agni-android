package com.latticeonfhir.android.data.server.api

import com.latticeonfhir.android.base.server.BaseResponse
import com.latticeonfhir.android.data.server.model.authentication.request.Login
import com.latticeonfhir.android.data.server.model.authentication.request.Otp
import com.latticeonfhir.android.data.server.model.authentication.response.LoginResponse
import com.latticeonfhir.android.data.server.model.authentication.response.OtpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthenticationApiService {

    @POST("auth/verifyUser")
    suspend fun login(@Body login: Login): Response<BaseResponse<LoginResponse>>

    @POST("auth/otp")
    suspend fun validateOtp(@Body otp: Otp): Response<BaseResponse<OtpResponse>>
}