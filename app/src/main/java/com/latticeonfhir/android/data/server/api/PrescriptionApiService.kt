package com.latticeonfhir.android.data.server.api

import com.latticeonfhir.android.base.server.BaseResponse
import com.latticeonfhir.android.data.server.model.create.CreateResponse
import com.latticeonfhir.android.data.server.model.prescription.medication.MedicationResponse
import com.latticeonfhir.android.data.server.model.prescription.medication.MedicineTimeResponse
import com.latticeonfhir.android.data.server.model.prescription.prescriptionresponse.PrescriptionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.QueryMap

@JvmSuppressWildcards
interface PrescriptionApiService {

    @GET("Medication")
    suspend fun getAllMedications(@QueryMap(encoded = true) map: Map<String,String>?): Response<BaseResponse<List<MedicationResponse>>>

    @POST("sync/{endPoint}")
    suspend fun postPrescriptionRelatedData(@Path("endPoint") endPoint: String, @Body prescriptionData: List<Any>): Response<BaseResponse<List<CreateResponse>>>

    @GET("MedicationRequest")
    suspend fun getPastPrescription(@QueryMap(encoded = true) map: Map<String,String>?): Response<BaseResponse<List<PrescriptionResponse>>>

    @GET("sct/medTime")
    suspend fun getMedicineTime(): Response<BaseResponse<List<MedicineTimeResponse>>>
}