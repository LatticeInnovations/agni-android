package com.latticeonfhir.android.data.local.roomdb.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import androidx.room.Update
import com.latticeonfhir.android.data.local.roomdb.entities.patient.IdentifierEntity
import com.latticeonfhir.android.data.local.roomdb.entities.patient.PatientAndIdentifierEntity
import com.latticeonfhir.android.data.local.roomdb.entities.patient.PatientEntity

@Dao
interface PatientDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatientData(vararg patientEntity: PatientEntity): List<Long>

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIdentifiers(vararg identifierEntity: IdentifierEntity): List<Long>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM PatientEntity INNER JOIN PatientLastUpdatedEntity ON PatientEntity.id = PatientLastUpdatedEntity.patientId ORDER BY PatientLastUpdatedEntity.lastUpdated DESC")
    fun getListPatientData(): PagingSource<Int, PatientAndIdentifierEntity>

    @Transaction
    @Query("SELECT * FROM PatientEntity WHERE id IN (:patientId)")
    suspend fun getPatientDataById(vararg patientId: String): List<PatientAndIdentifierEntity>

    @Transaction
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePatientData(patientEntity: PatientEntity): Int

    @Transaction
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateIdentifiers(vararg listOfIdentifiers: IdentifierEntity)

    @Transaction
    @Query("UPDATE PatientEntity SET fhirId=:fhirId WHERE id=:id")
    suspend fun updateFhirId(id: String, fhirId: String): Int

    @Transaction
    @Query("SELECT id FROM PatientEntity WHERE fhirId=:fhirId")
    suspend fun getPatientIdByFhirId(fhirId: String): String?
}