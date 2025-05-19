package com.latticeonfhir.core.data.repository.local.medication

import com.latticeonfhir.core.database.dao.MedicationDao
import com.latticeonfhir.core.database.entities.medication.MedicationStrengthRelation
import com.latticeonfhir.core.database.entities.medication.MedicineTimingEntity
import com.latticeonfhir.core.model.server.prescription.medication.MedicationResponse
import com.latticeonfhir.core.network.utils.responseconverter.toMedicationResponse
import javax.inject.Inject

class MedicationRepositoryImpl @Inject constructor(private val medicationDao: MedicationDao) :
    MedicationRepository {

    override suspend fun getActiveIngredients(): List<String> {
        return medicationDao.getActiveIngredients()
    }

    override suspend fun getMedicationByActiveIngredient(activeIngredient: String): List<MedicationResponse> {
        return medicationDao.getMedicationByActiveIngredient(activeIngredient)
            .map { medicationStrengthRelation -> medicationStrengthRelation.toMedicationResponse() }
    }

    override suspend fun getAllMedicationDirections(): List<MedicineTimingEntity> {
        return medicationDao.getAllMedicineDosageInstructions()
    }

    override suspend fun getAllMedication(): List<MedicationStrengthRelation> {
        return medicationDao.getAllMedication()
    }
    override suspend fun getOTCMedication(): List<MedicationStrengthRelation> {
        return medicationDao.getOTCMedication()
    }
}