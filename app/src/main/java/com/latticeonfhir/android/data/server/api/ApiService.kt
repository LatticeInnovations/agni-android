package com.latticeonfhir.android.data.server.api

import com.latticeonfhir.android.base.server.BaseResponse
import com.latticeonfhir.android.data.server.model.create.CreateResponse
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface ApiService {

    @GET("{endPoint}")
    suspend fun getListData(@Path("endPoint") endPoint: String, @QueryMap map: Map<String,String>?): Response<BaseResponse<List<PatientResponse>>>

    @POST("{endPoint}")
    suspend fun <T> createData(@Path("endPoint") endPoint: String, @Body patientResponses: List<T>): Response<BaseResponse<List<CreateResponse>>>

    @PATCH("{endPoint}/{id}")
    suspend fun <T> patchSingleChanges(@Path("endPoint") endPoint: String, @Path("id") id: String, patchLogs: Map<String,T>): Response<BaseResponse<PatientResponse>>

    @PATCH("sync/{endPoint}")
    suspend fun <T> patchListOfChanges(@Path("endPoint") endPoint: String, patchLogs: List<T>): Response<BaseResponse<List<CreateResponse>>>
}