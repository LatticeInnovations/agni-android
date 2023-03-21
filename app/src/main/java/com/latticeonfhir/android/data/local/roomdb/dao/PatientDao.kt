package com.latticeonfhir.android.data.local.roomdb.dao

import androidx.paging.PagingData
import androidx.paging.PagingSource
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
    suspend fun insertPatientData(vararg patientEntity: PatientEntity): List<Long>

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIdentifiers(vararg identifierEntity: IdentifierEntity): List<Long>

    @Transaction
    @Query("SELECT * FROM PatientEntity")
    fun getListPatientData(): PagingSource<Int,PatientAndIdentifierEntity>

    @Transaction
    @Query("SELECT * FROM PatientEntity WHERE id=:patientId")
    suspend fun getPatientDataById(patientId: String): PatientAndIdentifierEntity

    @Transaction
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePatientData(patientEntity: PatientEntity): Int

    @Transaction
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateIdentifiers(vararg listOfIdentifiers: IdentifierEntity)
}