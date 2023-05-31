package com.latticeonfhir.android.data.local.repository.prescription

import com.latticeonfhir.android.data.local.model.prescription.PrescriptionResponseLocal
import com.latticeonfhir.android.data.local.roomdb.entities.prescription.PrescriptionAndMedicineRelation
import com.latticeonfhir.android.data.server.model.prescription.prescriptionresponse.PrescriptionResponse

interface PrescriptionRepository {

    suspend fun insertPrescription(prescriptionResponseLocal: PrescriptionResponseLocal): Long
    suspend fun getLastPrescription(patientId: String): List<PrescriptionAndMedicineRelation>
}