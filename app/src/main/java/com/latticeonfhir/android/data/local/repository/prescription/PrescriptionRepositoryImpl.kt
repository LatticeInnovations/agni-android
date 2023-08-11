package com.latticeonfhir.android.data.local.repository.prescription

import com.latticeonfhir.android.data.local.model.prescription.PrescriptionResponseLocal
import com.latticeonfhir.android.data.local.roomdb.dao.PrescriptionDao
import com.latticeonfhir.android.data.local.roomdb.entities.prescription.PrescriptionAndMedicineRelation
import com.latticeonfhir.android.utils.converters.responseconverter.toListOfPrescriptionDirectionsEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toPrescriptionEntity
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

    override suspend fun getLastPrescription(patientId: String): List<PrescriptionAndMedicineRelation> {
        return prescriptionDao.getPastPrescriptions(patientId)
    }

    override suspend fun getPrescriptionByAppointmentId(appointmentId: String): List<PrescriptionResponseLocal> {
        return prescriptionDao.getPrescriptionByAppointmentId(appointmentId).map {prescriptionAndMedicineRelation ->
            prescriptionAndMedicineRelation.toPrescriptionResponseLocal()
        }
    }
}