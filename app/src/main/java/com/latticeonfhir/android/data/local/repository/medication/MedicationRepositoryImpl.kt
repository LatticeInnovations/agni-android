package com.latticeonfhir.android.data.local.repository.medication

import com.latticeonfhir.android.data.local.roomdb.dao.MedicationDao
import com.latticeonfhir.android.data.local.roomdb.entities.medication.MedicineDosageInstructionsEntity
import com.latticeonfhir.android.data.server.model.prescription.medication.Medication
import com.latticeonfhir.android.utils.converters.responseconverter.toMedication
import javax.inject.Inject

class MedicationRepositoryImpl @Inject constructor(private val medicationDao: MedicationDao): MedicationRepository {

    override suspend fun getMedicationByActiveIngredient(activeIngredient: String): List<Medication> {
        return medicationDao.getMedicationByActiveIngredient(activeIngredient).map { medicationEntity -> medicationEntity.toMedication()  }
    }

    override suspend fun getAllMedicationDirections(): List<MedicineDosageInstructionsEntity> {
        return medicationDao.getAllMedicineDosageInstructions()
    }
}