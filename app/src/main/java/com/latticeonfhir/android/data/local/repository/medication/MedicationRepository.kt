package com.latticeonfhir.android.data.local.repository.medication

import com.latticeonfhir.android.data.local.roomdb.entities.medication.MedicineDosageInstructionsEntity
import com.latticeonfhir.android.data.server.model.prescription.medication.Medication

interface MedicationRepository {

    suspend fun getMedicationByActiveIngredient(activeIngredient: String): List<Medication>
    suspend fun getAllMedicationDirections(): List<MedicineDosageInstructionsEntity>
}