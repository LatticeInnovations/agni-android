package com.latticeonfhir.android.data.local.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.latticeonfhir.android.data.local.roomdb.entities.medication.MedicationEntity
import com.latticeonfhir.android.data.local.roomdb.entities.medication.MedicineTimingEntity

@Dao
interface MedicationDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedication(vararg medicationEntity: MedicationEntity): List<Long>

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
    suspend fun getMedicationByActiveIngredient(activeIngredient: String): List<MedicationEntity>
}