package com.latticeonfhir.core.network.api

import com.latticeonfhir.core.model.base.BaseResponse
import com.latticeonfhir.core.model.server.create.CreateResponse
import com.latticeonfhir.core.model.server.dispense.response.DispenseData
import com.latticeonfhir.core.model.server.dispense.response.MedicineDispenseResponse
import com.latticeonfhir.core.network.constants.EndPoints.DISPENSE_LOG
import com.latticeonfhir.core.network.constants.EndPoints.MEDICATION_DISPENSE
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.QueryMap

@JvmSuppressWildcards
interface DispenseApiService {
    @GET(MEDICATION_DISPENSE)
    suspend fun getDispenseRecords(@QueryMap(encoded = true) map: Map<String, String>?): Response<BaseResponse<List<MedicineDispenseResponse>>>

    @POST("sync/{endPoint}")
    suspend fun postDispenseData(
        @Path("endPoint") endPoint: String,
        @Body dispenseData: List<Any>
    ): Response<BaseResponse<List<CreateResponse>>>

    @GET(DISPENSE_LOG)
    suspend fun getOTCRecords(@QueryMap(encoded = true) map: Map<String, String>?): Response<BaseResponse<List<DispenseData>>>
}