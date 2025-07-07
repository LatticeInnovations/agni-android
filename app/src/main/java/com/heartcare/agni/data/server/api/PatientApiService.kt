package com.heartcare.agni.data.server.api

import com.heartcare.agni.base.server.BaseResponse
import com.heartcare.agni.data.server.model.create.CreateResponse
import com.heartcare.agni.data.server.model.patient.PatientLastUpdatedResponse
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.data.server.model.relatedperson.RelatedPersonResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.QueryMap

@JvmSuppressWildcards
interface PatientApiService {

    @GET("{endPoint}")
    suspend fun getListData(
        @Path("endPoint") endPoint: String,
        @QueryMap(encoded = true) map: Map<String, String>?
    ): Response<BaseResponse<List<PatientResponse>>>

    @POST("{endPoint}")
    suspend fun createData(
        @Path("endPoint") endPoint: String,
        @Body patientResponses: List<Any>
    ): Response<BaseResponse<List<CreateResponse>>>

    @PATCH("{endPoint}/{id}")
    suspend fun patchSingleChanges(
        @Path("endPoint") endPoint: String,
        @Path("id") id: String,
        @Body patchLogs: Map<String, Any>
    ): Response<BaseResponse<PatientResponse>>

    @PATCH("{endPoint}")
    suspend fun patchListOfChanges(
        @Path("endPoint") endPoint: String,
        @Body patchLogs: List<Map<String, Any>>
    ): Response<BaseResponse<List<CreateResponse>>>

    @GET("{endPoint}")
    suspend fun getRelationData(
        @Path("endPoint") endPoint: String,
        @QueryMap(encoded = true) map: Map<String, String>?
    ): Response<BaseResponse<List<RelatedPersonResponse>>>

    @POST("timestamp")
    suspend fun postPatientLastUpdates(
        @Body patientLastUpdateData: List<Any>
    ): Response<BaseResponse<List<CreateResponse>>>

    @GET("timestamp")
    suspend fun getPatientLastUpdatedData(): Response<BaseResponse<List<PatientLastUpdatedResponse>>>
}