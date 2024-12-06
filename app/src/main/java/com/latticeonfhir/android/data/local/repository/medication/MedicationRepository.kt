package com.latticeonfhir.android.data.local.repository.medication

import com.latticeonfhir.android.data.local.roomdb.entities.medication.MedicationStrengthRelation
import com.latticeonfhir.android.data.local.roomdb.entities.medication.MedicineTimingEntity
import com.latticeonfhir.android.data.server.model.prescription.medication.MedicationResponse

interface MedicationRepository {

    suspend fun getActiveIngredients(): List<String>
    suspend fun getMedicationByActiveIngredient(activeIngredient: String): List<MedicationResponse>
    suspend fun getAllMedicationDirections(): List<MedicineTimingEntity>
    suspend fun getAllMedication(): List<MedicationStrengthRelation>
    suspend fun getOTCMedication(): List<MedicationStrengthRelation>
}