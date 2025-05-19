package com.latticeonfhir.core.data.repository.local.labtest

import com.latticeonfhir.core.database.dao.AppointmentDao
import com.latticeonfhir.core.database.dao.FileUploadDao
import com.latticeonfhir.core.database.dao.LabTestAndMedDao
import com.latticeonfhir.core.model.local.labtest.LabTestLocal
import com.latticeonfhir.core.model.local.labtest.LabTestPhotoResponseLocal
import com.latticeonfhir.core.model.server.prescription.photo.File
import com.latticeonfhir.core.network.utils.responseconverter.toFilesList
import com.latticeonfhir.core.network.utils.responseconverter.toLabTestAndMedEntity
import com.latticeonfhir.core.network.utils.responseconverter.toLabTestLocal
import com.latticeonfhir.core.network.utils.responseconverter.toLabTestPhotoResponseLocal
import com.latticeonfhir.core.network.utils.responseconverter.toListOfLabTestPhotoEntity
import javax.inject.Inject

class LabTestRepositoryImpl @Inject constructor(
    private val labTestAndMedDao: LabTestAndMedDao,
    private val fileUploadDao: FileUploadDao,
    private val appointmentDao: AppointmentDao
) : LabTestRepository {

    override suspend fun insertPhotoLabTestAndMed(
        local: LabTestPhotoResponseLocal,
        type: String
    ): Long {
        return labTestAndMedDao.insertLabAndMedTest(local.toLabTestAndMedEntity(type))[0].also {
            labTestAndMedDao.insertLabTestsAndMedPhotos(
                *local.toListOfLabTestPhotoEntity().toTypedArray()
            )
        }
    }


    override suspend fun getLastLabTestAndMed(appointmentId: String): LabTestLocal {
        return labTestAndMedDao.getLabTestAndMed(appointmentId).toLabTestLocal()
    }

    override suspend fun getLastPhotoLabAndMedTest(
        patientId: String,
        photoviewType: String
    ): List<File> {
        val listOfFiles = mutableListOf<File>()
        labTestAndMedDao.getPastPhotoLabAndMedTests(patientId, photoviewType).map {
            listOfFiles.addAll(it.toFilesList())
        }
        return listOfFiles.sortedBy {
            it.filename.substringBefore(".").toLong()
        }
    }


    override suspend fun getLabTestAndMedPhotoByAppointmentId(
        patientId: String,
        photoviewType: String
    ): List<LabTestPhotoResponseLocal> {
        return labTestAndMedDao.getLabTestAndMedPhotoByPatientId(
            patientId = patientId,
            photoviewType
        )
            .map {
                it.toLabTestPhotoResponseLocal(appointmentDao)
            }
    }

    override suspend fun getLabTestAndPhotoByDate(
        patientId: String, photoviewType: String, startDate: Long, endDate: Long
    ): LabTestPhotoResponseLocal {
        return labTestAndMedDao.getLabTestAndPhotoByDate(
            patientId,
            photoviewType,
            startDate,
            endDate
        )
            .map { it.toLabTestPhotoResponseLocal(appointmentDao) }[0]
    }

    override suspend fun insertLabTestAndPhotos(
        labTestPhotoResponseLocal: LabTestPhotoResponseLocal,
        type: String
    ): Long {
        return labTestAndMedDao.insertLabTestsAndMedPhotos(
            *labTestPhotoResponseLocal.toListOfLabTestPhotoEntity().toTypedArray()
        )[0]
    }

    override suspend fun deleteLabTestAndPhotos(
        labTestPhotoResponseLocal: LabTestPhotoResponseLocal,
        type: String
    ): Int {
        fileUploadDao.deleteFile(labTestPhotoResponseLocal.labTests[0].filename)
        return labTestAndMedDao.deleteLabTestAndMedPhoto(labTestPhotoResponseLocal.toListOfLabTestPhotoEntity()[0])
            .also {
                labTestAndMedDao.deleteLabTestAndMedEntity(
                    labTestPhotoResponseLocal.toLabTestAndMedEntity(
                        type
                    )
                )
            }
    }

}