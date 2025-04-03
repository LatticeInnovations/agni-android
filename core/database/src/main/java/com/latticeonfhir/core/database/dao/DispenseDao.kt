package com.latticeonfhir.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.latticeonfhir.core.database.entities.dispense.DispenseAndPrescriptionRelation
import com.latticeonfhir.core.database.entities.dispense.DispenseDataEntity
import com.latticeonfhir.core.database.entities.dispense.DispensePrescriptionEntity
import com.latticeonfhir.core.database.entities.dispense.DispensedPrescriptionInfo
import com.latticeonfhir.core.database.entities.dispense.MedicineDispenseListEntity

@Dao
interface DispenseDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrescriptionDispenseData(vararg dispensePrescriptionEntity: DispensePrescriptionEntity): List<Long>

    @Transaction
    @Query("SELECT * FROM DispensePrescriptionEntity INNER JOIN PrescriptionEntity ON DispensePrescriptionEntity.prescriptionId = PrescriptionEntity.id WHERE DispensePrescriptionEntity.patientId=:patientId ORDER BY PrescriptionEntity.prescriptionDate DESC")
    suspend fun getPrescriptionDispenseData(patientId: String): List<DispenseAndPrescriptionRelation>

    @Transaction
    @Query("SELECT * FROM DispensePrescriptionEntity WHERE prescriptionId=:prescriptionId")
    suspend fun getPrescriptionDispenseDataById(prescriptionId: String): DispenseAndPrescriptionRelation

    @Transaction
    @Query("SELECT * FROM DispenseDataEntity WHERE prescriptionId=:prescriptionId ORDER BY generatedOn DESC")
    suspend fun getDispensedPrescriptionInfo(prescriptionId: String): List<DispensedPrescriptionInfo>

    @Transaction
    @Query("SELECT * FROM DispenseDataEntity WHERE patientId=:patientId ORDER BY generatedOn DESC")
    suspend fun getDispensedPrescriptionInfoByPatientId(patientId: String): List<DispensedPrescriptionInfo>

    @Transaction
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateDispenseStatus(dispensePrescriptionEntity: DispensePrescriptionEntity): Int

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDispenseDataEntity(vararg dispenseDataEntity: DispenseDataEntity): List<Long>

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicineDispenseDataList(vararg medicineDispenseListEntity: MedicineDispenseListEntity): List<Long>

    @Transaction
    @Query("UPDATE DispenseDataEntity SET dispenseFhirId = :dispenseFhirId WHERE dispenseId = :dispenseId")
    suspend fun updateDispenseFhirId(dispenseId: String, dispenseFhirId: String)

    @Transaction
    @Query("UPDATE MedicineDispenseListEntity SET medDispenseFhirId = :medDispenseFhirId WHERE medDispenseUuid = :medDispenseId")
    suspend fun updateMedicineDispenseFhirId(medDispenseId: String, medDispenseFhirId: String)

    @Transaction
    @Query("SELECT dispenseId FROM DispenseDataEntity WHERE dispenseFhirId=:fhirId")
    suspend fun getDispenseIdByFhirId(fhirId: String): String

    @Transaction
    @Query("SELECT * FROM DispensePrescriptionEntity")
    suspend fun getAllDispense(): List<DispensePrescriptionEntity>
}