package com.latticeonfhir.android.data.server.api

import com.latticeonfhir.android.base.server.BaseResponse
import com.latticeonfhir.android.data.server.model.create.CreateResponse
import com.latticeonfhir.android.data.server.model.cvd.CVDResponse
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

    @GET("sync/CVD")
    suspend fun getCVD(
        @QueryMap(encoded = true) map: Map<String, String>?
    ): Response<BaseResponse<List<CVDResponse>>>

    @PATCH("sync/CVD")
    @JvmSuppressWildcards
    suspend fun patchListOfChanges(
        @Body patchLogs: List<Map<String, Any>>
    ): Response<BaseResponse<List<CreateResponse>>>
}