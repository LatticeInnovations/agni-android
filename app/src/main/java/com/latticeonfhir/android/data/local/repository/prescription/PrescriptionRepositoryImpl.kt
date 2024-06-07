package com.latticeonfhir.android.data.local.repository.prescription

import com.latticeonfhir.android.data.local.model.prescription.PrescriptionPhotoResponseLocal
import com.latticeonfhir.android.data.local.model.prescription.PrescriptionResponseLocal
import com.latticeonfhir.android.data.local.roomdb.dao.PrescriptionDao
import com.latticeonfhir.android.data.local.roomdb.entities.prescription.PrescriptionAndMedicineRelation
import com.latticeonfhir.android.data.server.model.prescription.photo.File
import com.latticeonfhir.android.data.server.model.prescription.photo.PrescriptionPhotoResponse
import com.latticeonfhir.android.utils.converters.responseconverter.toFilesList
import com.latticeonfhir.android.utils.converters.responseconverter.toListOfPrescriptionDirectionsEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toListOfPrescriptionPhotoEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toPrescriptionEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toPrescriptionPhotoResponse
import com.latticeonfhir.android.utils.converters.responseconverter.toPrescriptionResponseLocal
import javax.inject.Inject

class PrescriptionRepositoryImpl @Inject constructor(private val prescriptionDao: PrescriptionDao) :
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

    override suspend fun getLastPrescription(patientId: String): List<PrescriptionAndMedicineRelation> {
        return prescriptionDao.getPastPrescriptions(patientId)
    }

    override suspend fun getLastPhotoPrescription(patientId: String): List<File> {
        val listOfFiles = mutableListOf<File>()
        prescriptionDao.getPastPhotoPrescriptions(patientId).map {
            listOfFiles.addAll(it.toFilesList())
        }
        return listOfFiles
    }

    override suspend fun getPrescriptionByAppointmentId(appointmentId: String): List<PrescriptionResponseLocal> {
        return prescriptionDao.getPrescriptionByAppointmentId(appointmentId)
            .map { prescriptionAndMedicineRelation ->
                prescriptionAndMedicineRelation.toPrescriptionResponseLocal()
            }
    }

    override suspend fun getPrescriptionPhotoByAppointmentId(appointmentId: String): List<PrescriptionPhotoResponse> {
        return prescriptionDao.getPrescriptionPhotoByAppointmentId(appointmentId)
            .map {
                it.toPrescriptionPhotoResponse()
            }
    }
}