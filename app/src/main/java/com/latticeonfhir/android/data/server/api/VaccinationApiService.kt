package com.latticeonfhir.core.data.server.api

import com.latticeonfhir.core.base.server.BaseResponse
import com.latticeonfhir.android.data.server.constants.EndPoints.IMMUNIZATION
import com.latticeonfhir.android.data.server.constants.EndPoints.IMMUNIZATION_RECOMMENDATION
import com.latticeonfhir.core.data.server.constants.EndPoints.VACCINE_MANUFACTURER
import com.latticeonfhir.core.data.server.model.create.CreateResponse
import com.latticeonfhir.core.data.server.model.vaccination.ImmunizationRecommendationResponse
import com.latticeonfhir.core.data.server.model.vaccination.ImmunizationResponse
import com.latticeonfhir.core.data.server.model.vaccination.ManufacturerResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.QueryMap

interface VaccinationApiService {

    @POST("sync/$IMMUNIZATION")
    suspend fun postImmunization(@Body immunizationResponse: List<ImmunizationResponse>): Response<com.latticeonfhir.core.base.server.BaseResponse<List<CreateResponse>>>

    @GET(IMMUNIZATION)
    suspend fun getAllImmunization(@QueryMap(encoded = true) map: Map<String, String>?): Response<com.latticeonfhir.android.base.server.BaseResponse<List<ImmunizationResponse>>>

    @GET(IMMUNIZATION_RECOMMENDATION)
    suspend fun getAllImmunizationRecommendation(@QueryMap(encoded = true) map: Map<String, String>?): Response<com.latticeonfhir.android.base.server.BaseResponse<List<ImmunizationRecommendationResponse>>>

    @GET(VACCINE_MANUFACTURER)
    suspend fun getAllManufacturers(@QueryMap(encoded = true) map: Map<String, String>?): Response<com.latticeonfhir.core.base.server.BaseResponse<List<ManufacturerResponse>>>
}