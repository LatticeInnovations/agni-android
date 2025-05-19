package com.latticeonfhir.core.data.repository.local.vaccination.impl

import com.latticeonfhir.core.data.repository.local.vaccination.ImmunizationRepository
import com.latticeonfhir.core.database.dao.vaccincation.ImmunizationDao
import com.latticeonfhir.core.database.dao.vaccincation.ImmunizationRecommendationDao
import com.latticeonfhir.core.database.dao.vaccincation.ManufacturerDao
import com.latticeonfhir.core.database.entities.vaccination.ImmunizationFileEntity
import com.latticeonfhir.core.model.entity.vaccination.ManufacturerEntity
import com.latticeonfhir.core.model.local.vaccination.Immunization
import com.latticeonfhir.core.utils.converters.responseconverter.Vaccination.toImmunizationEntity
import java.util.Date
import javax.inject.Inject

class ImmunizationRepositoryImpl @Inject constructor(
    private val immunizationDao: ImmunizationDao,
    private val manufacturerDao: ManufacturerDao,
    private val immunizationRecommendationDao: ImmunizationRecommendationDao
) : ImmunizationRepository {

    override suspend fun insertImmunization(immunization: Immunization): List<Long> {
        return immunizationDao.insertImmunization(immunization.toImmunizationEntity()).also {
            if (immunization.filename?.isNotEmpty() == true) {
                immunizationDao.insertImmunizationFiles(
                    *immunization.filename!!.map { filename ->
                        ImmunizationFileEntity(
                            filename = filename,
                            immunizationId = immunization.id
                        )
                    }.toTypedArray()
                )
            }
        }
    }

    override suspend fun getImmunization(patientId: String): List<Immunization> {
        return immunizationDao.getImmunizationByPatientId(patientId).map { immunizationEntity ->
            val filenames = immunizationDao.getFileNameByImmunizationId(immunizationEntity.id)
            val manufacturer = immunizationEntity.manufacturerId?.let {
                manufacturerDao.getManufacturerById(
                    it
                )
            }
            val immunizationRecommendation =
                immunizationRecommendationDao.getImmunizationRecommendationByVaccineCode(
                    immunizationEntity.vaccineCode
                )
            Immunization(
                id = immunizationEntity.id,
                vaccineName = immunizationRecommendation.vaccine,
                vaccineSortName = immunizationRecommendation.vaccineShortName,
                vaccineCode = immunizationRecommendation.vaccineCode,
                lotNumber = immunizationEntity.lotNumber,
                takenOn = immunizationEntity.createdOn,
                expiryDate = immunizationEntity.expiryDate,
                manufacturer = manufacturer as ManufacturerEntity?,
                notes = immunizationEntity.notes,
                filename = filenames.map { it.filename },
                patientId = immunizationEntity.patientId,
                appointmentId = immunizationEntity.appointmentId
            )
        }
    }

    override suspend fun getImmunizationByTime(createdOn: Date): Immunization {
        val immunizationEntity = immunizationDao.getImmunizationByTime(createdOn.time)
        val filenames = immunizationDao.getFileNameByImmunizationId(immunizationEntity.id)
        val manufacturer = immunizationEntity.manufacturerId?.let {
            manufacturerDao.getManufacturerById(
                it
            )
        }
        val immunizationRecommendation =
            immunizationRecommendationDao.getImmunizationRecommendationByVaccineCode(
                immunizationEntity.vaccineCode
            )
        return Immunization(
            id = immunizationEntity.id,
            vaccineName = immunizationRecommendation.vaccine,
            vaccineSortName = immunizationRecommendation.vaccineShortName,
            vaccineCode = immunizationRecommendation.vaccineCode,
            lotNumber = immunizationEntity.lotNumber,
            takenOn = immunizationEntity.createdOn,
            expiryDate = immunizationEntity.expiryDate,
            manufacturer = manufacturer as ManufacturerEntity?,
            notes = immunizationEntity.notes,
            filename = filenames.map { it.filename },
            patientId = immunizationEntity.patientId,
            appointmentId = immunizationEntity.appointmentId
        )
    }
}