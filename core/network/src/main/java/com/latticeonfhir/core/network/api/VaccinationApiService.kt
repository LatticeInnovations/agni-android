package com.latticeonfhir.core.network.api

import com.latticeonfhir.core.model.base.BaseResponse
import com.latticeonfhir.core.model.server.create.CreateResponse
import com.latticeonfhir.core.model.server.vaccination.ImmunizationRecommendationResponse
import com.latticeonfhir.core.model.server.vaccination.ImmunizationResponse
import com.latticeonfhir.core.model.server.vaccination.ManufacturerResponse
import com.latticeonfhir.core.network.constants.EndPoints.IMMUNIZATION
import com.latticeonfhir.core.network.constants.EndPoints.IMMUNIZATION_RECOMMENDATION
import com.latticeonfhir.core.network.constants.EndPoints.VACCINE_MANUFACTURER
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.QueryMap

interface VaccinationApiService {

    @POST("sync/$IMMUNIZATION")
    suspend fun postImmunization(@Body immunizationResponse: List<ImmunizationResponse>): Response<BaseResponse<List<CreateResponse>>>

    @GET(IMMUNIZATION)
    suspend fun getAllImmunization(@QueryMap(encoded = true) map: Map<String, String>?): Response<BaseResponse<List<ImmunizationResponse>>>

    @GET(IMMUNIZATION_RECOMMENDATION)
    suspend fun getAllImmunizationRecommendation(@QueryMap(encoded = true) map: Map<String, String>?): Response<BaseResponse<List<ImmunizationRecommendationResponse>>>

    @GET(VACCINE_MANUFACTURER)
    suspend fun getAllManufacturers(@QueryMap(encoded = true) map: Map<String, String>?): Response<BaseResponse<List<ManufacturerResponse>>>
}