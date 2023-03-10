package com.latticeonfhir.android.data.local.roomdb.dao

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.latticeonfhir.android.data.local.roomdb.entities.GenericEntity

interface GenericDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChangeRequest(genericEntity: GenericEntity): Long

    @Transaction
    @Query("SELECT payload FROM GenericEntity WHERE id=:id")
    suspend fun getChangeRequestPayload(id: String): String?
}