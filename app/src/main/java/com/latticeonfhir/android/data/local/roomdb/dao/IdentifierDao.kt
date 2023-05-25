package com.latticeonfhir.android.data.local.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.latticeonfhir.android.data.local.roomdb.entities.patient.IdentifierEntity

@Dao
interface IdentifierDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListOfIdentifier(identifierEntityList: List<IdentifierEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIdentifier(identifierEntity: IdentifierEntity): Long
}