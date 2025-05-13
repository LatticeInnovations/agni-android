package com.latticeonfhir.core.network.api

import com.latticeonfhir.core.data.local.model.vital.VitalLocal
import com.latticeonfhir.core.model.base.BaseResponse
import com.latticeonfhir.core.model.server.create.CreateResponse
import com.latticeonfhir.core.model.server.vitals.VitalResponse
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