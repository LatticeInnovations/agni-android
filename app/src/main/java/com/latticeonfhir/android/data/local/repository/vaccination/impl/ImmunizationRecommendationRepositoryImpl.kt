package com.latticeonfhir.android.data.local.repository.vaccination.impl

import com.latticeonfhir.android.data.local.model.vaccination.ImmunizationRecommendation
import com.latticeonfhir.android.data.local.repository.vaccination.ImmunizationRecommendationRepository
import com.latticeonfhir.android.data.local.roomdb.dao.vaccincation.ImmunizationDao
import com.latticeonfhir.android.data.local.roomdb.dao.vaccincation.ImmunizationRecommendationDao
import com.latticeonfhir.android.data.local.roomdb.entities.vaccination.ImmunizationRecommendationEntity
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
}