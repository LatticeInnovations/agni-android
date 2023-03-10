package com.latticeonfhir.android.data.server.api

import com.latticeonfhir.android.base.server.BaseResponse
import com.latticeonfhir.android.data.server.model.CreateResponse
import com.latticeonfhir.android.data.server.model.PatientResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.QueryMap
import java.util.Objects

interface ApiService {

    @GET("{{endPoint}}")
    suspend fun getListPatientData(@Path("endPoint") endPoint: String, @QueryMap map: Map<String,String>?): Response<BaseResponse<List<PatientResponse>>>

    @POST("{{endPoint}}")
    suspend fun createPatientData(@Path("endPoint") endPoint: String, @Body patientResponses: List<PatientResponse>): Response<BaseResponse<List<CreateResponse>>>

    @PATCH("{{endPoint}}/{{id}}")
    suspend fun patchSinglePatientChanges(@Path("endPoint") endPoint: String, @Path("id") id: String, patchLogs: Map<String,Objects>): Response<BaseResponse<PatientResponse>>

    @PATCH("sync/{{endPoint}}")
    suspend fun patchListOfPatientChanges(@Path("endPoint") endPoint: String, patchLogs: List<Map<String,Objects>>): Response<BaseResponse<List<CreateResponse>>>
}