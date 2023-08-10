package com.latticeonfhir.android.data.local.repository.medication

import com.latticeonfhir.android.data.local.roomdb.dao.MedicationDao
import com.latticeonfhir.android.data.local.roomdb.entities.medication.MedicineTimingEntity
import com.latticeonfhir.android.data.server.model.prescription.medication.MedicationResponse
import com.latticeonfhir.android.utils.converters.responseconverter.toMedicationResponse
import javax.inject.Inject

class MedicationRepositoryImpl @Inject constructor(private val medicationDao: MedicationDao) :
    MedicationRepository {

    override suspend fun getActiveIngredients(): List<String> {
        return medicationDao.getActiveIngredients()
    }

    override suspend fun getMedicationByActiveIngredient(activeIngredient: String): List<MedicationResponse> {
        return medicationDao.getMedicationByActiveIngredient(activeIngredient)
            .map { medicationEntity -> medicationEntity.toMedicationResponse() }
    }

    override suspend fun getAllMedicationDirections(): List<MedicineTimingEntity> {
        return medicationDao.getAllMedicineDosageInstructions()
    }
}