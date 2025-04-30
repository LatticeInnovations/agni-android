package com.latticeonfhir.core.data.repository.local.vaccination.impl

import com.latticeonfhir.core.data.local.model.vaccination.ImmunizationRecommendation
import com.latticeonfhir.core.data.repository.local.vaccination.ImmunizationRecommendationRepository
import com.latticeonfhir.core.database.dao.vaccincation.ImmunizationDao
import com.latticeonfhir.core.database.dao.vaccincation.ImmunizationRecommendationDao
import com.latticeonfhir.core.database.entities.vaccination.ImmunizationRecommendationEntity
import javax.inject.Inject

class ImmunizationRecommendationRepositoryImpl @Inject constructor(
    private val immunizationRecommendationDao: ImmunizationRecommendationDao,
    private val immunizationDao: ImmunizationDao
): ImmunizationRecommendationRepository {

    override suspend fun getImmunizationRecommendation(patientId: String): List<ImmunizationRecommendation> {
        return immunizationRecommendationDao.getImmunizationRecommendationByPatientId(patientId)
            .groupBy { immunizationRecommendationEntity -> immunizationRecommendationEntity.vaccineCode }
            .flatMap { map: Map.Entry<String, List<ImmunizationRecommendationEntity>> ->
                val takenOnDates = immunizationDao.getVaccineTakenDate(patientId, map.key)
                map.value.map { immunizationRecommendationEntity ->
                    ImmunizationRecommendation(
                        id = immunizationRecommendationEntity.id,
                        name = immunizationRecommendationEntity.vaccine,
                        shortName = immunizationRecommendationEntity.vaccineShortName,
                        seriesDoses = immunizationRecommendationEntity.seriesDoses,
                        doseNumber = immunizationRecommendationEntity.doseNumber,
                        vaccineStartDate = immunizationRecommendationEntity.vaccineStartDate,
                        vaccineEndDate = immunizationRecommendationEntity.vaccineEndDate,
                        takenOn = takenOnDates.getOrNull(immunizationRecommendationEntity.doseNumber - 1),
                        vaccineCode = immunizationRecommendationEntity.vaccineCode,
                        vaccineDueDate = immunizationRecommendationEntity.vaccineDueDate
                    )
                }
            }
    }

    override suspend fun clearImmunizationRecommendationOfPatient(patientId: String): Int {
        return immunizationRecommendationDao.clearImmunizationRecommendationOfPatient(patientId)
    }
}