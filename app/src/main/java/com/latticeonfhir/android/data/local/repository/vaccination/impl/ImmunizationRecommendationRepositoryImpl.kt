package com.latticeonfhir.android.data.local.repository.vaccination.impl

import com.latticeonfhir.android.data.local.model.vaccination.ImmunizationRecommendation
import com.latticeonfhir.android.data.local.repository.vaccination.ImmunizationRecommendationRepository
import com.latticeonfhir.android.data.local.roomdb.dao.vaccincation.ImmunizationDao
import com.latticeonfhir.android.data.local.roomdb.dao.vaccincation.ImmunizationRecommendationDao
import javax.inject.Inject

class ImmunizationRecommendationRepositoryImpl @Inject constructor(
    private val immunizationRecommendationDao: ImmunizationRecommendationDao,
    private val immunizationDao: ImmunizationDao
): ImmunizationRecommendationRepository {

    override suspend fun getImmunizationRecommendation(patientId: String): List<ImmunizationRecommendation> {
        return immunizationRecommendationDao.getImmunizationRecommendationByPatientId(patientId).map { immunizationRecommendationEntity ->
            ImmunizationRecommendation(
                id = immunizationRecommendationEntity.id,
                name = immunizationRecommendationEntity.vaccine,
                shortName = immunizationRecommendationEntity.vaccineShortName,
                seriesDoses = immunizationRecommendationEntity.seriesDoses,
                doseNumber = immunizationRecommendationEntity.doseNumber,
                vaccineStartDate = immunizationRecommendationEntity.vaccineStartDate,
                vaccineEndDate = immunizationRecommendationEntity.vaccineEndDate,
                takenOn = immunizationDao.getVaccineTakenDate(patientId, immunizationRecommendationEntity.vaccineCode)
            )
        }
    }
}