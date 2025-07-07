package com.heartcare.agni.data.server.api

import com.heartcare.agni.base.server.BaseResponse
import com.heartcare.agni.data.local.model.symdiag.SymptomsAndDiagnosisData
import com.heartcare.agni.data.server.model.create.CreateResponse
import com.heartcare.agni.data.server.model.symptomsanddiagnosis.Diagnosis
import com.heartcare.agni.data.server.model.symptomsanddiagnosis.Symptoms
import com.heartcare.agni.data.server.model.symptomsanddiagnosis.SymptomsAndDiagnosisResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface SymptomsAndDiagnosisService {

    @GET("SympDx/list?name=symptomsList")
    suspend fun getSymptoms(): Response<BaseResponse<List<Symptoms>>>

    @GET("SympDx/list?name=diagnosisList")
    suspend fun getDiagnosis(): Response<BaseResponse<List<Diagnosis>>>

    @GET("{endPoint}")
    suspend fun getListData(
        @Path("endPoint") endPoint: String,
        @QueryMap(encoded = true) map: Map<String, String>?
    ): Response<BaseResponse<List<SymptomsAndDiagnosisResponse>>>

    @POST("{endPoint}")
    suspend fun createData(
        @Path("endPoint") endPoint: String,
        @Body symDiag: List<SymptomsAndDiagnosisData>
    ): Response<BaseResponse<List<CreateResponse>>>

    @PATCH("{endPoint}")
    @JvmSuppressWildcards
    suspend fun patchListOfChanges(
        @Path("endPoint") endPoint: String,
        @Body patchLogs: List<Map<String, Any>>
    ): Response<BaseResponse<List<CreateResponse>>>

}