package com.latticeonfhir.android.data.local.repository.vaccination

import com.latticeonfhir.android.data.local.model.vaccination.ImmunizationRecommendation

interface ImmunizationRecommendationRepository {

    suspend fun getImmunizationRecommendation(patientId: String): List<ImmunizationRecommendation>
    suspend fun clearImmunizationRecommendationOfPatient(patientId: String): Int
}