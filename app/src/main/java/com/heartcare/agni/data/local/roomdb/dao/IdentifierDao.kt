package com.heartcare.agni.data.local.roomdb.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import com.heartcare.agni.data.local.roomdb.entities.patient.IdentifierEntity

@Dao
interface IdentifierDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListOfIdentifier(identifierEntityList: List<IdentifierEntity>)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIdentifier(identifierEntity: IdentifierEntity): Long

    @Transaction
    @Delete
    suspend fun deleteIdentifier(vararg identifierEntity: IdentifierEntity): Int
}