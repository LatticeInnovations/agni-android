package com.latticeonfhir.android.data.server.api

import com.latticeonfhir.android.base.server.BaseResponse
import com.latticeonfhir.android.data.local.model.symdiag.SymptomsAndDiagnosisData
import com.latticeonfhir.android.data.server.model.create.CreateResponse
import com.latticeonfhir.android.data.server.model.symptomsanddiagnosis.Diagnosis
import com.latticeonfhir.android.data.server.model.symptomsanddiagnosis.Symptoms
import com.latticeonfhir.android.data.server.model.symptomsanddiagnosis.SymptomsAndDiagnosisResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface SymptomsAndDiagnosisService {

    @GET("ValueSet?name=symptomsList")
    suspend fun getSymptoms(): Response<BaseResponse<List<Symptoms>>>

    @GET("ValueSet?name=diagnosisList")
    suspend fun getDiagnosis(): Response<BaseResponse<List<Diagnosis>>>

    @GET("{endPoint}")
    suspend fun getListData(
        @Path("endPoint") endPoint: String,
        @QueryMap(encoded = true) map: Map<String, String>?
    ): Response<BaseResponse<List<SymptomsAndDiagnosisResponse>>>

    @POST("sync/{endPoint}")
    suspend fun createData(
        @Path("endPoint") endPoint: String,
        @Body symDiag: List<SymptomsAndDiagnosisData>
    ): Response<BaseResponse<List<CreateResponse>>>

    @PATCH("sync/{endPoint}")
    @JvmSuppressWildcards
    suspend fun patchListOfChanges(
        @Path("endPoint") endPoint: String,
        @Body patchLogs: List<Map<String, Any>>
    ): Response<BaseResponse<List<CreateResponse>>>

}