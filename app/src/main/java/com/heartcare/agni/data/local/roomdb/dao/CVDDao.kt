package com.heartcare.agni.data.local.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.heartcare.agni.data.local.roomdb.entities.cvd.CVDEntity

@Dao
interface CVDDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCVDRecord(vararg cvdEntity: CVDEntity): List<Long>

    @Query("SELECT * FROM CVDEntity WHERE patientId=:patientId ORDER BY createdOn DESC")
    fun getCVDRecords(patientId: String): List<CVDEntity>

    @Query("SELECT * FROM CVDEntity WHERE patientId=:patientId AND createdOn BETWEEN :startTime AND :endTime")
    fun getTodayCVDRecords(patientId: String, startTime: Long, endTime: Long): CVDEntity?

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateCVDRecord(cvdEntity: CVDEntity): Int

    @Transaction
    @Query("UPDATE CVDEntity SET cvdFhirId=:fhirId WHERE cvdUuid=:id")
    suspend fun updateCVDFhirId(id: String, fhirId: String): Int

}