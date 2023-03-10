package com.latticeonfhir.android.data.local.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.latticeonfhir.android.data.local.roomdb.entities.IdentifierEntity
import com.latticeonfhir.android.data.local.roomdb.entities.PatientAndIdentifierEntity
import com.latticeonfhir.android.data.local.roomdb.entities.PatientEntity

@Dao
interface PatientDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListPatientData(list: List<PatientEntity>)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListOfIdentifiers(vararg listOfIdentifiers: IdentifierEntity)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatientData(patientEntity: PatientEntity): Long

    @Transaction
    @Query("SELECT * FROM PatientEntity")
    suspend fun getListPatientData(): List<PatientAndIdentifierEntity>

    @Transaction
    @Query("SELECT * FROM PatientEntity WHERE id=:PatientId")
    suspend fun getPatientDataById(PatientId: String): PatientAndIdentifierEntity

    @Transaction
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePatientData(patientEntity: PatientEntity): Int

    @Transaction
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateListOfIdentifiers(vararg listOfIdentifiers: IdentifierEntity)
}