package com.latticeonfhir.android.data.local.repository.prescription

import com.latticeonfhir.android.data.local.model.prescription.PrescriptionPhotoResponseLocal
import com.latticeonfhir.android.data.local.model.prescription.PrescriptionResponseLocal
import com.latticeonfhir.android.data.local.roomdb.entities.prescription.PrescriptionAndMedicineRelation

interface PrescriptionRepository {

    suspend fun insertPrescription(prescriptionResponseLocal: PrescriptionResponseLocal): Long
    suspend fun insertPhotoPrescription(prescriptionPhotoResponseLocal: PrescriptionPhotoResponseLocal): Long
    suspend fun getLastPrescription(patientId: String): List<PrescriptionAndMedicineRelation>
    suspend fun getPrescriptionByAppointmentId(appointmentId: String): List<PrescriptionResponseLocal>
}