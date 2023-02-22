package com.latticeonfhir.android.data.server.api

import com.latticeonfhir.android.base.server.BaseResponse
import com.latticeonfhir.android.data.server.model.PersonResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("Person")
    suspend fun getListPersonData(): Response<BaseResponse<List<PersonResponse>>>

    @GET("Person/_id{{id}}")
    suspend fun getPersonDataById(@Path("id") id: String): Response<BaseResponse<List<PersonResponse>>>
}