package com.latticeonfhir.android.data.server.api

import com.latticeonfhir.android.base.server.BaseResponse
import com.latticeonfhir.android.data.server.model.authentication.FacilityResponse
import com.latticeonfhir.android.data.server.model.authentication.Login
import com.latticeonfhir.android.data.server.model.authentication.Otp
import com.latticeonfhir.android.data.server.model.authentication.TokenResponse
import com.latticeonfhir.android.utils.constants.AuthenticationConstants.TEMP_TOKEN
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthenticationApiService {

    @POST("auth/login")
    suspend fun login(@Body login: Login): Response<BaseResponse<String?>>

    @POST("auth/otp")
    suspend fun validateOtp(@Body otp: Otp): Response<BaseResponse<TokenResponse>>

    @GET("facilities/{id}")
    suspend fun getFacilityDetails(
        @Path("id") facilityId: String
    ): Response<BaseResponse<FacilityResponse>>

    @DELETE("user")
    suspend fun deleteUserDetails(@Header(TEMP_TOKEN) tempToken: String): Response<BaseResponse<String?>>
}