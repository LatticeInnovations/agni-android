package com.latticeonfhir.android.data.local.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.latticeonfhir.android.data.local.roomdb.entities.cvd.CVDEntity

@Dao
interface CVDDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCVDRecord(vararg cvdEntity: CVDEntity): List<Long>

    @Query("SELECT * FROM CVDEntity WHERE patientId=:patientId ORDER BY createdOn DESC")
    fun getCVDRecords(patientId: String): List<CVDEntity>
}