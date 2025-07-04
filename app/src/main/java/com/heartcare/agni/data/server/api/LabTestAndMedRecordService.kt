package com.heartcare.agni.data.server.api

import com.heartcare.agni.data.server.model.labormed.medicalrecord.MedicalRecordResponse
import com.heartcare.agni.base.server.BaseResponse
import com.heartcare.agni.data.server.model.create.CreateResponse
import com.heartcare.agni.data.server.model.labormed.labtest.LabTestResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface LabTestAndMedRecordService {

    @GET("{endPoint}")
    suspend fun getListData(
        @Path("endPoint") endPoint: String,
        @QueryMap(encoded = true) map: Map<String, String>?
    ): Response<BaseResponse<List<LabTestResponse>>>

    @GET("{endPoint}")
    suspend fun getListMedicalRecordData(
        @Path("endPoint") endPoint: String, @QueryMap(encoded = true) map: Map<String, String>?
    ): Response<BaseResponse<List<MedicalRecordResponse>>>

    @POST("sync/{endPoint}")
    @JvmSuppressWildcards
    suspend fun createData(
        @Path("endPoint") endPoint: String,
        @Body labOrMed: List<Any>
    ): Response<BaseResponse<List<CreateResponse>>>

    @PATCH("sync/DocumentReference")
    @JvmSuppressWildcards
    suspend fun patchListOfChanges(
        @Body patchLogs: List<Map<String, Any>>
    ): Response<BaseResponse<List<CreateResponse>>>

    @HTTP(method = "DELETE", path = "sync/{endPoint}", hasBody = true)
    @JvmSuppressWildcards
    suspend fun deleteLabOrMedicalRecordPhoto(
        @Path("endPoint") endPoint: String,@Body patchLogs: List<Any>): Response<BaseResponse<List<CreateResponse>>>
}