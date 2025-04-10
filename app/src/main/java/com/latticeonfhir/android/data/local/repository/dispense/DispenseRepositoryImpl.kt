package com.latticeonfhir.core.data.local.repository.dispense

import com.latticeonfhir.core.data.local.roomdb.dao.DispenseDao
import com.latticeonfhir.core.data.local.roomdb.entities.dispense.DispenseAndPrescriptionRelation
import com.latticeonfhir.android.data.local.roomdb.entities.dispense.DispenseDataEntity
import com.latticeonfhir.core.data.local.roomdb.entities.dispense.DispensePrescriptionEntity
import com.latticeonfhir.core.data.local.roomdb.entities.dispense.DispensedPrescriptionInfo
import com.latticeonfhir.core.data.local.roomdb.entities.dispense.MedicineDispenseListEntity
import javax.inject.Inject

class DispenseRepositoryImpl @Inject constructor(
    private val dispenseDao: DispenseDao
): DispenseRepository {
    override suspend fun insertPrescriptionDispenseData(dispensePrescriptionEntity: DispensePrescriptionEntity): List<Long> {
        return dispenseDao.insertPrescriptionDispenseData(dispensePrescriptionEntity)
    }

    override suspend fun getPrescriptionDispenseData(patientId: String): List<DispenseAndPrescriptionRelation> {
        return dispenseDao.getPrescriptionDispenseData(patientId)
    }

    override suspend fun getPrescriptionDispenseDataById(prescriptionId: String): DispenseAndPrescriptionRelation {
        return dispenseDao.getPrescriptionDispenseDataById(prescriptionId)
    }

    override suspend fun getDispensedPrescriptionInfo(prescriptionId: String): List<DispensedPrescriptionInfo> {
        return dispenseDao.getDispensedPrescriptionInfo(prescriptionId)
    }

    override suspend fun getDispensedPrescriptionInfoByPatientId(patientId: String): List<DispensedPrescriptionInfo> {
        return dispenseDao.getDispensedPrescriptionInfoByPatientId(patientId)
    }

    override suspend fun updateDispenseStatus(dispensePrescriptionEntity: DispensePrescriptionEntity): Int {
        return dispenseDao.updateDispenseStatus(dispensePrescriptionEntity)
    }

    override suspend fun insertDispenseData(
        dispenseDataEntity: DispenseDataEntity,
        medicineDispenseListEntityList: List<MedicineDispenseListEntity>
    ): List<Long> {
        return dispenseDao.insertDispenseDataEntity(dispenseDataEntity).also {
            dispenseDao.insertMedicineDispenseDataList(*medicineDispenseListEntityList.toTypedArray())
        }
    }
}