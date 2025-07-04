package com.heartcare.agni.data.server.api

import com.heartcare.agni.base.server.BaseResponse
import com.heartcare.agni.data.server.constants.EndPoints.DISPENSE_LOG
import com.heartcare.agni.data.server.constants.EndPoints.MEDICATION_DISPENSE
import com.heartcare.agni.data.server.model.create.CreateResponse
import com.heartcare.agni.data.server.model.dispense.response.DispenseData
import com.heartcare.agni.data.server.model.dispense.response.MedicineDispenseResponse
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

    @POST("{endPoint}")
    suspend fun postDispenseData(
        @Path("endPoint") endPoint: String,
        @Body dispenseData: List<Any>
    ): Response<BaseResponse<List<CreateResponse>>>

    @GET(DISPENSE_LOG)
    suspend fun getOTCRecords(@QueryMap(encoded = true) map: Map<String, String>?): Response<BaseResponse<List<DispenseData>>>
}