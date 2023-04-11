package com.latticeonfhir.android.data.local.roomdb.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.latticeonfhir.android.data.local.roomdb.entities.PatientAndIdentifierEntity

@Dao
interface SearchDao {

    @Transaction
    @Query("SELECT * FROM PatientEntity")
    suspend fun getPatientList(): List<PatientAndIdentifierEntity>
}