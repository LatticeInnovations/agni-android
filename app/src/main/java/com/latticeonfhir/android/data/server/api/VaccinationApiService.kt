package com.latticeonfhir.android.data.server.api

import com.latticeonfhir.android.base.server.BaseResponse
import com.latticeonfhir.android.data.server.model.vaccination.ImmunizationRecommendationResponse
import retrofit2.Response
import retrofit2.http.GET

interface VaccinationApiService {

    @GET("/ImmunizationRecommendation")
    suspend fun getAllImmunizationRecommendation(): Response<BaseResponse<ImmunizationRecommendationResponse>>
}