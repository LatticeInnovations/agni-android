package com.latticeonfhir.android.data.local.repository.prescription

import com.latticeonfhir.android.data.local.model.prescription.PrescriptionPhotoResponseLocal
import com.latticeonfhir.android.data.local.model.prescription.PrescriptionResponseLocal
import com.latticeonfhir.android.data.local.roomdb.entities.prescription.PrescriptionAndMedicineRelation
import com.latticeonfhir.android.data.local.roomdb.entities.prescription.photo.PrescriptionAndFileEntity
import com.latticeonfhir.android.data.local.roomdb.entities.prescription.photo.PrescriptionPhotoEntity
import com.latticeonfhir.android.data.server.model.prescription.photo.PrescriptionPhotoResponse

interface PrescriptionRepository {

    suspend fun insertPrescription(prescriptionResponseLocal: PrescriptionResponseLocal): Long
    suspend fun insertPhotoPrescription(prescriptionPhotoResponseLocal: PrescriptionPhotoResponseLocal): Long
    suspend fun getLastPrescription(patientId: String): List<PrescriptionAndMedicineRelation>
    suspend fun getLastPhotoPrescription(patientId: String): List<PrescriptionAndFileEntity>
    suspend fun getPrescriptionByAppointmentId(appointmentId: String): List<PrescriptionResponseLocal>
    suspend fun getPrescriptionPhotoByAppointmentId(appointmentId: String): List<PrescriptionPhotoResponse>
    suspend fun getPrescriptionPhotoByDate(
        patientId: String,
        startDate: Long,
        endDate: Long
    ): PrescriptionPhotoResponse

    suspend fun insertPrescriptionPhotos(prescriptionPhotoEntity: PrescriptionPhotoEntity): Long
    suspend fun deletePrescriptionPhotos(prescriptionPhotoEntity: PrescriptionPhotoEntity): Int
}