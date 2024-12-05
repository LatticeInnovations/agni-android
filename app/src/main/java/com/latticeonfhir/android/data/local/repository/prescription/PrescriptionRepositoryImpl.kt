package com.latticeonfhir.android.data.local.repository.prescription

import com.latticeonfhir.android.data.local.model.prescription.PrescriptionPhotoResponseLocal
import com.latticeonfhir.android.data.local.model.prescription.PrescriptionResponseLocal
import com.latticeonfhir.android.data.local.roomdb.dao.FileUploadDao
import com.latticeonfhir.android.data.local.roomdb.dao.PrescriptionDao
import com.latticeonfhir.android.utils.converters.responseconverter.toListOfPrescriptionDirectionsEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toListOfPrescriptionPhotoEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toPrescriptionEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toPrescriptionPhotoResponseLocal
import com.latticeonfhir.android.utils.converters.responseconverter.toPrescriptionResponseLocal
import javax.inject.Inject

class PrescriptionRepositoryImpl @Inject constructor(
    private val prescriptionDao: PrescriptionDao,
    private val fileUploadDao: FileUploadDao
) :
    PrescriptionRepository {

    override suspend fun insertPrescription(prescriptionResponseLocal: PrescriptionResponseLocal): Long {
        return prescriptionDao.insertPrescription(prescriptionResponseLocal.toPrescriptionEntity())[0].also {
            prescriptionDao.insertPrescriptionMedicines(
                *prescriptionResponseLocal.toListOfPrescriptionDirectionsEntity().toTypedArray()
            )
        }
    }

    override suspend fun insertPhotoPrescription(prescriptionPhotoResponseLocal: PrescriptionPhotoResponseLocal): Long {
        return prescriptionDao.insertPrescription(prescriptionPhotoResponseLocal.toPrescriptionEntity())[0].also {
            prescriptionDao.insertPrescriptionPhotos(
                *prescriptionPhotoResponseLocal.toListOfPrescriptionPhotoEntity().toTypedArray()
            )
        }
    }

    override suspend fun getLastPrescription(patientId: String): List<PrescriptionResponseLocal> {
        return prescriptionDao.getPastPrescriptions(patientId).map { it.toPrescriptionResponseLocal() }
    }

    override suspend fun getLastPhotoPrescription(patientId: String): List<PrescriptionPhotoResponseLocal> {
        return prescriptionDao.getPastPhotoPrescriptions(patientId).map { it.toPrescriptionPhotoResponseLocal() }
    }

    override suspend fun getPrescriptionByAppointmentId(appointmentId: String): List<PrescriptionResponseLocal> {
        return prescriptionDao.getPrescriptionByAppointmentId(appointmentId)
            .map { prescriptionAndMedicineRelation ->
                prescriptionAndMedicineRelation.toPrescriptionResponseLocal()
            }
    }

    override suspend fun getPrescriptionPhotoByAppointmentId(appointmentId: String): List<PrescriptionPhotoResponseLocal> {
        return prescriptionDao.getPrescriptionPhotoByAppointmentId(appointmentId)
            .map {
                it.toPrescriptionPhotoResponseLocal()
            }
    }

    override suspend fun getPrescriptionPhotoByDate(
        patientId: String,
        startDate: Long,
        endDate: Long
    ): PrescriptionPhotoResponseLocal {
        return prescriptionDao.getPrescriptionPhotoByDate(patientId, startDate, endDate)
            .map { it.toPrescriptionPhotoResponseLocal() }[0]
    }

    override suspend fun getPrescriptionPhotoById(prescriptionId: String): PrescriptionPhotoResponseLocal {
        return prescriptionDao.getPrescriptionPhotoById(prescriptionId)
            .toPrescriptionPhotoResponseLocal()
    }

    override suspend fun insertPrescriptionPhotos(prescriptionPhotoResponseLocal: PrescriptionPhotoResponseLocal): Long {
        return prescriptionDao.insertPrescriptionPhotos(
            *prescriptionPhotoResponseLocal.toListOfPrescriptionPhotoEntity().toTypedArray()
        )[0]
    }

    override suspend fun deletePhotoPrescription(prescriptionPhotoResponseLocal: PrescriptionPhotoResponseLocal): Int {
        fileUploadDao.deleteFile(prescriptionPhotoResponseLocal.prescription[0].filename)
        return prescriptionDao.deletePrescriptionPhoto(prescriptionPhotoResponseLocal.toListOfPrescriptionPhotoEntity()[0]).also {
            prescriptionDao.deletePrescriptionEntity(prescriptionPhotoResponseLocal.toPrescriptionEntity())
        }
    }
}