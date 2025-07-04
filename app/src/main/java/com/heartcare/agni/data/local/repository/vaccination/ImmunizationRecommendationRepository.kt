package com.heartcare.agni.data.local.repository.vaccination

import com.heartcare.agni.data.local.model.vaccination.ImmunizationRecommendation

interface ImmunizationRecommendationRepository {

    suspend fun getImmunizationRecommendation(patientId: String): List<ImmunizationRecommendation>
    suspend fun clearImmunizationRecommendationOfPatient(patientId: String): Int
}