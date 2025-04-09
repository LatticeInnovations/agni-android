package com.latticeonfhir.core.data.local.repository.prescription

import com.latticeonfhir.core.data.local.model.prescription.PrescriptionPhotoResponseLocal
import com.latticeonfhir.core.data.local.model.prescription.PrescriptionResponseLocal

interface PrescriptionRepository {

    suspend fun insertPrescription(prescriptionResponseLocal: PrescriptionResponseLocal): Long
    suspend fun insertPhotoPrescription(prescriptionPhotoResponseLocal: PrescriptionPhotoResponseLocal): Long
    suspend fun getLastPrescription(patientId: String): List<PrescriptionResponseLocal>
    suspend fun getLastPhotoPrescription(patientId: String): List<PrescriptionPhotoResponseLocal>
    suspend fun getPrescriptionByAppointmentId(appointmentId: String): List<PrescriptionResponseLocal>
    suspend fun getPrescriptionPhotoByAppointmentId(appointmentId: String): List<PrescriptionPhotoResponseLocal>
    suspend fun getPrescriptionPhotoByDate(
        patientId: String,
        startDate: Long,
        endDate: Long
    ): PrescriptionPhotoResponseLocal
    suspend fun getPrescriptionPhotoById(prescriptionId: String): PrescriptionPhotoResponseLocal

    suspend fun insertPrescriptionPhotos(prescriptionPhotoResponseLocal: PrescriptionPhotoResponseLocal): Long
    suspend fun deletePhotoPrescription(prescriptionPhotoResponseLocal: PrescriptionPhotoResponseLocal): Int
}