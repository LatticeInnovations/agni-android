package com.latticeonfhir.core.data.repository.local.dispense

import com.latticeonfhir.core.data.local.roomdb.entities.dispense.DispenseAndPrescriptionRelation
import com.latticeonfhir.android.data.local.roomdb.entities.dispense.DispenseDataEntity
import com.latticeonfhir.android.data.local.roomdb.entities.dispense.DispensePrescriptionEntity
import com.latticeonfhir.core.data.local.roomdb.entities.dispense.DispensedPrescriptionInfo
import com.latticeonfhir.core.data.local.roomdb.entities.dispense.MedicineDispenseListEntity

interface DispenseRepository {
    suspend fun insertPrescriptionDispenseData(dispensePrescriptionEntity: DispensePrescriptionEntity): List<Long>
    suspend fun getPrescriptionDispenseData(patientId: String): List<DispenseAndPrescriptionRelation>
    suspend fun getPrescriptionDispenseDataById(prescriptionId: String): DispenseAndPrescriptionRelation
    suspend fun getDispensedPrescriptionInfo(prescriptionId: String): List<DispensedPrescriptionInfo>
    suspend fun getDispensedPrescriptionInfoByPatientId(patientId: String): List<DispensedPrescriptionInfo>
    suspend fun updateDispenseStatus(dispensePrescriptionEntity: DispensePrescriptionEntity): Int
    suspend fun insertDispenseData(dispenseDataEntity: DispenseDataEntity, medicineDispenseListEntityList: List<MedicineDispenseListEntity>): List<Long>
}