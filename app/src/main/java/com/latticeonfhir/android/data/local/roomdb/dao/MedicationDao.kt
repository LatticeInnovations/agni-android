package com.latticeonfhir.android.data.local.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.latticeonfhir.core.data.local.roomdb.entities.medication.MedicationEntity
import com.latticeonfhir.core.data.local.roomdb.entities.medication.MedicationStrengthRelation
import com.latticeonfhir.core.data.local.roomdb.entities.medication.MedicineTimingEntity
import com.latticeonfhir.core.data.local.roomdb.entities.medication.StrengthEntity

@Dao
interface MedicationDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedication(vararg medicationEntity: MedicationEntity): List<Long>

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStrength(vararg strengthEntity: StrengthEntity): List<Long>

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicineDosageInstructions(vararg medicineTimingEntity: MedicineTimingEntity): List<Long>

    @Transaction
    @Query("SELECT DISTINCT activeIngredient FROM MedicationEntity")
    suspend fun getActiveIngredients(): List<String>

    @Transaction
    @Query("SELECT * FROM MedicineTimingEntity")
    suspend fun getAllMedicineDosageInstructions(): List<MedicineTimingEntity>

    @Transaction
    @Query("SELECT * FROM MedicationEntity WHERE activeIngredient = :activeIngredient")
    suspend fun getMedicationByActiveIngredient(activeIngredient: String): List<MedicationStrengthRelation>

    @Transaction
    @Query("SELECT medicalDosage FROM MedicineTimingEntity WHERE medicalDosageId=:medicalDosageId")
    suspend fun getMedicalDosageByMedicalDosageId(medicalDosageId: String): String

    @Transaction
    @Query("SELECT * FROM MedicationEntity")
    suspend fun getAllMedication(): List<MedicationStrengthRelation>

    @Transaction
    @Query("SELECT * FROM MedicationEntity WHERE isOTC=1")
    suspend fun getOTCMedication(): List<MedicationStrengthRelation>
}