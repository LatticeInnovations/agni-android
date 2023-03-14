package com.latticeonfhir.android.data.local.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.roomdb.entities.GenericEntity

@Dao
interface GenericDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChangeRequest(genericEntity: GenericEntity): Long

    @Transaction
    @Query("SELECT * FROM GenericEntity WHERE id=:id AND type=:genericTypeEnum")
    suspend fun getGenericEntityById(id: String, genericTypeEnum: GenericTypeEnum): GenericEntity?

    @Transaction
    @Query("SELECT payload FROM GenericEntity WHERE id=:id")
    suspend fun getChangeRequestPayloadById(id: String): String?
}