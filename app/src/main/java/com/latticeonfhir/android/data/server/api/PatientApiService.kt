package com.latticeonfhir.core.data.server.api

import com.latticeonfhir.core.base.server.BaseResponse
import com.latticeonfhir.core.data.server.model.create.CreateResponse
import com.latticeonfhir.core.data.server.model.patient.PatientLastUpdatedResponse
import com.latticeonfhir.core.data.server.model.patient.PatientResponse
import com.latticeonfhir.core.data.server.model.relatedperson.RelatedPersonResponse
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
    ): Response<com.latticeonfhir.core.base.server.BaseResponse<List<PatientResponse>>>

    @POST("sync/{endPoint}")
    suspend fun createData(
        @Path("endPoint") endPoint: String,
        @Body patientResponses: List<Any>
    ): Response<com.latticeonfhir.android.base.server.BaseResponse<List<CreateResponse>>>

    @PATCH("{endPoint}/{id}")
    suspend fun patchSingleChanges(
        @Path("endPoint") endPoint: String,
        @Path("id") id: String,
        @Body patchLogs: Map<String, Any>
    ): Response<com.latticeonfhir.android.base.server.BaseResponse<PatientResponse>>

    @PATCH("sync/{endPoint}")
    suspend fun patchListOfChanges(
        @Path("endPoint") endPoint: String,
        @Body patchLogs: List<Map<String, Any>>
    ): Response<com.latticeonfhir.core.base.server.BaseResponse<List<CreateResponse>>>

    @GET("{endPoint}")
    suspend fun getRelationData(
        @Path("endPoint") endPoint: String,
        @QueryMap(encoded = true) map: Map<String, String>?
    ): Response<com.latticeonfhir.android.base.server.BaseResponse<List<RelatedPersonResponse>>>

    @POST("timestamp")
    suspend fun postPatientLastUpdates(
        @Body patientLastUpdateData: List<Any>
    ): Response<com.latticeonfhir.android.base.server.BaseResponse<List<CreateResponse>>>

    @GET("timestamp")
    suspend fun getPatientLastUpdatedData(): Response<com.latticeonfhir.core.base.server.BaseResponse<List<PatientLastUpdatedResponse>>>
}