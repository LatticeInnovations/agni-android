package com.heartcare.agni.data.server.api

import com.heartcare.agni.base.server.BaseResponse
import com.heartcare.agni.data.server.constants.EndPoints.IMMUNIZATION
import com.heartcare.agni.data.server.constants.EndPoints.IMMUNIZATION_RECOMMENDATION
import com.heartcare.agni.data.server.constants.EndPoints.VACCINE_MANUFACTURER
import com.heartcare.agni.data.server.model.create.CreateResponse
import com.heartcare.agni.data.server.model.vaccination.ImmunizationRecommendationResponse
import com.heartcare.agni.data.server.model.vaccination.ImmunizationResponse
import com.heartcare.agni.data.server.model.vaccination.ManufacturerResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.QueryMap

interface VaccinationApiService {

    @POST(IMMUNIZATION)
    suspend fun postImmunization(@Body immunizationResponse: List<ImmunizationResponse>): Response<BaseResponse<List<CreateResponse>>>

    @GET(IMMUNIZATION)
    suspend fun getAllImmunization(@QueryMap(encoded = true) map: Map<String, String>?): Response<BaseResponse<List<ImmunizationResponse>>>

    @GET(IMMUNIZATION_RECOMMENDATION)
    suspend fun getAllImmunizationRecommendation(@QueryMap(encoded = true) map: Map<String, String>?): Response<BaseResponse<List<ImmunizationRecommendationResponse>>>

    @GET(VACCINE_MANUFACTURER)
    suspend fun getAllManufacturers(@QueryMap(encoded = true) map: Map<String, String>?): Response<BaseResponse<List<ManufacturerResponse>>>
}