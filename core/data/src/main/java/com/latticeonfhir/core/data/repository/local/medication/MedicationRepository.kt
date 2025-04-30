package com.latticeonfhir.core.data.repository.local.medication

import com.latticeonfhir.core.database.entities.medication.MedicationStrengthRelation
import com.latticeonfhir.core.database.entities.medication.MedicineTimingEntity
import com.latticeonfhir.core.model.server.prescription.medication.MedicationResponse

interface MedicationRepository {

    suspend fun getActiveIngredients(): List<String>
    suspend fun getMedicationByActiveIngredient(activeIngredient: String): List<MedicationResponse>
    suspend fun getAllMedicationDirections(): List<MedicineTimingEntity>
    suspend fun getAllMedication(): List<MedicationStrengthRelation>
    suspend fun getOTCMedication(): List<MedicationStrengthRelation>
}