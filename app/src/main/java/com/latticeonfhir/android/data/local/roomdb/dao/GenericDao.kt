package com.latticeonfhir.android.data.local.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.enums.SyncType
import com.latticeonfhir.android.data.local.roomdb.entities.GenericEntity

@Dao
interface GenericDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGenericEntity(vararg genericEntity: GenericEntity): List<Long>

    @Transaction
    @Query("SELECT * FROM GenericEntity WHERE patientId=:patientId AND type=:genericTypeEnum AND syncType=:syncType")
    suspend fun getGenericEntityById(patientId: String, genericTypeEnum: GenericTypeEnum, syncType: SyncType): GenericEntity?

    @Transaction
    @Query("SELECT payload FROM GenericEntity WHERE id=:id")
    suspend fun getChangeRequestPayloadById(id: String): String?

    @Transaction
    @Query("SELECT * FROM GenericEntity WHERE type=:genericTypeEnum AND syncType=:syncType LIMIT :limit")
    suspend fun getSameTypeGenericEntityPayload(genericTypeEnum: GenericTypeEnum, syncType: SyncType, limit: Int = 10): List<GenericEntity>

    @Transaction
    @Query("DELETE FROM GenericEntity WHERE id IN (:ids)")
    suspend fun deleteSyncPayload(ids: List<String>): Int

    @Transaction
    @Query("SELECT * FROM GenericEntity WHERE type=:genericTypeEnum AND syncType=:syncType")
    suspend fun getNotSyncedPostRelation(genericTypeEnum: GenericTypeEnum = GenericTypeEnum.RELATION, syncType: SyncType = SyncType.POST): List<GenericEntity>
}