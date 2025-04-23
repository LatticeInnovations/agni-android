package com.latticeonfhir.core.network.api

import com.latticeonfhir.android.base.server.BaseResponse
import com.latticeonfhir.core.model.server.create.CreateResponse
import com.latticeonfhir.core.model.server.cvd.CVDResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.QueryMap

@JvmSuppressWildcards
interface CVDApiService {

    @POST("sync/CVD")
    suspend fun createCVD(@Body cvdResponse: List<CVDResponse>): Response<BaseResponse<List<CreateResponse>>>

    @GET("CVD")
    suspend fun getCVD(
        @QueryMap(encoded = true) map: Map<String, String>?
    ): Response<BaseResponse<List<CVDResponse>>>

    @PATCH("sync/CVD")
    @JvmSuppressWildcards
    suspend fun patchListOfChanges(
        @Body patchLogs: List<Map<String, Any>>
    ): Response<BaseResponse<List<CreateResponse>>>
}