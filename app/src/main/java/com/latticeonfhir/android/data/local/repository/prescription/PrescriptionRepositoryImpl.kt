package com.latticeonfhir.android.data.local.repository.prescription

import com.latticeonfhir.android.data.local.model.prescription.PrescriptionResponseLocal
import com.latticeonfhir.android.data.local.roomdb.dao.PrescriptionDao
import com.latticeonfhir.android.data.local.roomdb.entities.prescription.PrescriptionAndMedicineRelation
import com.latticeonfhir.android.data.server.model.prescription.prescriptionresponse.PrescriptionResponse
import com.latticeonfhir.android.utils.converters.responseconverter.toListOfPrescriptionDirectionsEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toPrescriptionEntity
import javax.inject.Inject

class PrescriptionRepositoryImpl @Inject constructor(private val prescriptionDao: PrescriptionDao): PrescriptionRepository {

    override suspend fun insertPrescription(prescriptionResponseLocal: PrescriptionResponseLocal): Long {
        return prescriptionDao.insertPrescription(prescriptionResponseLocal.toPrescriptionEntity())[0].also {
            prescriptionDao.insertPrescriptionMedicines(*prescriptionResponseLocal.toListOfPrescriptionDirectionsEntity().toTypedArray())
        }
    }

    override suspend fun getLastPrescription(patientId: String): List<PrescriptionAndMedicineRelation> {
        return prescriptionDao.getPastPrescriptions(patientId)
    }
}