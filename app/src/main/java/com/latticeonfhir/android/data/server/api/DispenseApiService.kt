package com.latticeonfhir.core.data.server.api

import com.latticeonfhir.core.base.server.BaseResponse
import com.latticeonfhir.android.data.server.constants.EndPoints.DISPENSE_LOG
import com.latticeonfhir.android.data.server.constants.EndPoints.MEDICATION_DISPENSE
import com.latticeonfhir.android.data.server.model.create.CreateResponse
import com.latticeonfhir.android.data.server.model.dispense.response.DispenseData
import com.latticeonfhir.core.data.server.model.dispense.response.MedicineDispenseResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.QueryMap

@JvmSuppressWildcards
interface DispenseApiService {
    @GET(MEDICATION_DISPENSE)
    suspend fun getDispenseRecords(@QueryMap(encoded = true) map: Map<String, String>?): Response<com.latticeonfhir.core.base.server.BaseResponse<List<MedicineDispenseResponse>>>

    @POST("sync/{endPoint}")
    suspend fun postDispenseData(
        @Path("endPoint") endPoint: String,
        @Body dispenseData: List<Any>
    ): Response<com.latticeonfhir.core.base.server.BaseResponse<List<CreateResponse>>>

    @GET(DISPENSE_LOG)
    suspend fun getOTCRecords(@QueryMap(encoded = true) map: Map<String, String>?): Response<com.latticeonfhir.android.base.server.BaseResponse<List<DispenseData>>>
}