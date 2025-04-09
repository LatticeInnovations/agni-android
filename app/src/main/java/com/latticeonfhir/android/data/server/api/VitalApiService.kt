package com.latticeonfhir.core.data.server.api

import com.latticeonfhir.android.base.server.BaseResponse
import com.latticeonfhir.core.data.local.model.vital.VitalLocal
import com.latticeonfhir.core.data.server.model.create.CreateResponse
import com.latticeonfhir.android.data.server.model.vitals.VitalResponse
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
    ): Response<com.latticeonfhir.core.base.server.BaseResponse<List<VitalResponse>>>

    @POST("sync/{endPoint}")
    suspend fun createData(
        @Path("endPoint") endPoint: String, @Body vitals: List<VitalLocal>
    ): Response<com.latticeonfhir.android.base.server.BaseResponse<List<CreateResponse>>>

    @PATCH("sync/{endPoint}")
    @JvmSuppressWildcards
    suspend fun patchListOfChanges(
        @Path("endPoint") endPoint: String,
        @Body patchLogs: List<Map<String, Any>>
    ): Response<com.latticeonfhir.core.base.server.BaseResponse<List<CreateResponse>>>
}