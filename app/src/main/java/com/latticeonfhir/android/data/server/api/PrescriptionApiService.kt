package com.latticeonfhir.core.data.server.api

import com.latticeonfhir.android.base.server.BaseResponse
import com.latticeonfhir.core.data.server.model.create.CreateResponse
import com.latticeonfhir.core.data.server.model.prescription.medication.MedicationResponse
import com.latticeonfhir.core.data.server.model.prescription.medication.MedicineTimeResponse
import com.latticeonfhir.core.data.server.model.prescription.photo.PrescriptionPhotoResponse
import com.latticeonfhir.android.data.server.model.prescription.prescriptionresponse.PrescriptionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.QueryMap

@JvmSuppressWildcards
interface PrescriptionApiService {

    @GET("Medication")
    suspend fun getAllMedications(@QueryMap(encoded = true) map: Map<String, String>?): Response<com.latticeonfhir.core.base.server.BaseResponse<List<MedicationResponse>>>

    @POST("sync/{endPoint}")
    suspend fun postPrescriptionRelatedData(
        @Path("endPoint") endPoint: String,
        @Body prescriptionData: List<Any>
    ): Response<com.latticeonfhir.core.base.server.BaseResponse<List<CreateResponse>>>

    @GET("MedicationRequest")
    suspend fun getPastPrescription(@QueryMap(encoded = true) map: Map<String, String>?): Response<com.latticeonfhir.android.base.server.BaseResponse<List<PrescriptionResponse>>>

    @GET("PrescriptionFile")
    suspend fun getPastPhotoPrescription(@QueryMap(encoded = true) map: Map<String, String>?): Response<com.latticeonfhir.android.base.server.BaseResponse<List<PrescriptionPhotoResponse>>>

    @GET("sct/medTime")
    suspend fun getMedicineTime(@QueryMap(encoded = true) map: Map<String, String>?): Response<com.latticeonfhir.core.base.server.BaseResponse<List<MedicineTimeResponse>>>

    @PATCH("sync/DocumentReference")
    suspend fun patchListOfChanges(@Body patchLogs: List<Any>): Response<com.latticeonfhir.core.base.server.BaseResponse<List<CreateResponse>>>

    @HTTP(method = "DELETE", path = "sync/PrescriptionFile", hasBody = true)
    suspend fun deletePrescriptionPhoto(@Body patchLogs: List<Any>): Response<com.latticeonfhir.core.base.server.BaseResponse<List<CreateResponse>>>
}