package com.latticeonfhir.android.data.local.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.latticeonfhir.android.data.local.roomdb.entities.medication.MedicationEntity
import com.latticeonfhir.android.data.local.roomdb.entities.medication.MedicineDosageInstructionsEntity

@Dao
interface MedicationDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedication(vararg medicationEntity: MedicationEntity): List<Long>

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicineDosageInstructions(vararg medicineDosageInstructionsEntity: MedicineDosageInstructionsEntity): List<Long>

    @Transaction
    @Query("SELECT activeIngredient FROM MedicationEntity")
    suspend fun getActiveIngredients(): List<String>

    @Transaction
    @Query("SELECT * FROM MedicineDosageInstructionsEntity")
    suspend fun getAllMedicineDosageInstructions(): List<MedicineDosageInstructionsEntity>

    @Transaction
    @Query("SELECT * FROM MedicationEntity WHERE activeIngredient = :activeIngredient")
    suspend fun getMedicationByActiveIngredient(activeIngredient: String): List<MedicationEntity>
}