package com.latticeonfhir.android.data.local.repository.labtest

import com.latticeonfhir.core.data.local.model.labtest.LabTestLocal
import com.latticeonfhir.android.data.local.model.labtest.LabTestPhotoResponseLocal
import com.latticeonfhir.core.data.local.roomdb.dao.AppointmentDao
import com.latticeonfhir.android.data.local.roomdb.dao.FileUploadDao
import com.latticeonfhir.core.data.local.roomdb.dao.LabTestAndMedDao
import com.latticeonfhir.android.data.server.model.prescription.photo.File
import com.latticeonfhir.core.utils.converters.responseconverter.toFilesList
import com.latticeonfhir.android.utils.converters.responseconverter.toLabTestAndMedEntity
import com.latticeonfhir.core.utils.converters.responseconverter.toLabTestLocal
import com.latticeonfhir.core.utils.converters.responseconverter.toLabTestPhotoResponseLocal
import com.latticeonfhir.android.utils.converters.responseconverter.toListOfLabTestPhotoEntity
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