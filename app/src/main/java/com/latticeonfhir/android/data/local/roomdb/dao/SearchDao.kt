package com.latticeonfhir.android.data.local.roomdb.dao

import androidx.room.Dao
import androidx.room.Query
import com.latticeonfhir.android.data.local.roomdb.entities.PatientAndIdentifierEntity

@Dao
interface SearchDao {

    @Query("SELECT * FROM PatientEntity")
    suspend fun getPatientList(): List<PatientAndIdentifierEntity>
}