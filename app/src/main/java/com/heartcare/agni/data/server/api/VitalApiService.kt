package com.heartcare.agni.data.server.api

import com.heartcare.agni.base.server.BaseResponse
import com.heartcare.agni.data.local.model.vital.VitalLocal
import com.heartcare.agni.data.server.model.create.CreateResponse
import com.heartcare.agni.data.server.model.vitals.VitalResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface VitalApiService {
    @GET("{endPoint}")
    suspend fun getListData(
        @Path("endPoint") endPoint: String,
        @QueryMap(encoded = true) map: Map<String, String>?
    ): Response<BaseResponse<List<VitalResponse>>>

    @POST("sync/{endPoint}")
    suspend fun createData(
        @Path("endPoint") endPoint: String, @Body vitals: List<VitalLocal>
    ): Response<BaseResponse<List<CreateResponse>>>

    @PATCH("sync/{endPoint}")
    @JvmSuppressWildcards
    suspend fun patchListOfChanges(
        @Path("endPoint") endPoint: String,
        @Body patchLogs: List<Map<String, Any>>
    ): Response<BaseResponse<List<CreateResponse>>>
}