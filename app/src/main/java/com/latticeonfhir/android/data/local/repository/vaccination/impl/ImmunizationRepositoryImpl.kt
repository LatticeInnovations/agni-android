package com.latticeonfhir.android.data.local.repository.vaccination.impl

import com.latticeonfhir.android.data.local.model.vaccination.Immunization
import com.latticeonfhir.android.data.local.repository.vaccination.ImmunizationRepository
import com.latticeonfhir.android.data.local.roomdb.dao.vaccincation.ImmunizationDao
import com.latticeonfhir.android.data.local.roomdb.dao.vaccincation.ImmunizationRecommendationDao
import com.latticeonfhir.android.data.local.roomdb.dao.vaccincation.ManufacturerDao
import com.latticeonfhir.android.data.local.roomdb.entities.vaccination.ImmunizationFileEntity
import com.latticeonfhir.android.utils.converters.responseconverter.Vaccination.toImmunizationEntity
import javax.inject.Inject

class ImmunizationRepositoryImpl @Inject constructor(
    private val immunizationDao: ImmunizationDao,
    private val manufacturerDao: ManufacturerDao,
    private val immunizationRecommendationDao: ImmunizationRecommendationDao
) : ImmunizationRepository {

    override suspend fun insertImmunization(immunization: Immunization): List<Long> {
        return immunizationDao.insertImmunization(immunization.toImmunizationEntity()).also {
            if (immunization.filename?.isNotEmpty() == true){
                immunizationDao.insertImmunizationFiles(
                    *immunization.filename.map { filename ->
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
            val immunizationRecommendation = immunizationRecommendationDao.getImmunizationRecommendationByVaccineCode(immunizationEntity.vaccineCode)
            Immunization(
                id = immunizationEntity.id,
                vaccineName = immunizationRecommendation.vaccine,
                vaccineSortName = immunizationRecommendation.vaccineShortName,
                vaccineCode = immunizationRecommendation.vaccineCode,
                lotNumber = immunizationEntity.lotNumber,
                takenOn = immunizationEntity.createdOn,
                expiryDate = immunizationEntity.expiryDate,
                manufacturer = manufacturer,
                notes = immunizationEntity.notes,
                filename = filenames.map { it.filename },
                patientId = immunizationEntity.patientId,
                appointmentId = immunizationEntity.appointmentId
            )
        }
    }
}