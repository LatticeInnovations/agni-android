package com.latticeonfhir.android.data.local.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.latticeonfhir.android.data.local.roomdb.entities.medication.MedicationEntity
import com.latticeonfhir.android.data.local.roomdb.entities.medication.MedicineDosageInstructionsEntity

@Dao
interface MedicationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedication(medicationEntity: MedicationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicineDosageInstructions(medicineDosageInstructionsEntity: MedicineDosageInstructionsEntity): Long

    @Query("SELECT * FROM MedicineDosageInstructionsEntity")
    suspend fun getAllMedicineDosageInstructions(): List<MedicineDosageInstructionsEntity>

    @Query("SELECT * FROM MedicationEntity WHERE activeIngredient = :activeIngredient")
    suspend fun getMedicationByActiveIngredient(activeIngredient: String): List<MedicationEntity>
}