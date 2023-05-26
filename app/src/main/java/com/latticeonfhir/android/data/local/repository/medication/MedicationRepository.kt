package com.latticeonfhir.android.data.local.repository.medication

import com.latticeonfhir.android.data.local.roomdb.entities.medication.MedicineDosageInstructionsEntity
import com.latticeonfhir.android.data.server.model.prescription.medication.MedicationResponse

interface MedicationRepository {

    suspend fun getMedicationByActiveIngredient(activeIngredient: String): List<MedicationResponse>
    suspend fun getAllMedicationDirections(): List<MedicineDosageInstructionsEntity>
}