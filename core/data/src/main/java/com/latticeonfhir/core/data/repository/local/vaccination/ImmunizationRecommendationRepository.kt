package com.latticeonfhir.core.data.repository.local.vaccination

import com.latticeonfhir.core.data.local.model.vaccination.ImmunizationRecommendation

interface ImmunizationRecommendationRepository {

    suspend fun getImmunizationRecommendation(patientId: String): List<ImmunizationRecommendation>
    suspend fun clearImmunizationRecommendationOfPatient(patientId: String): Int
}