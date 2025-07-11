package com.latticeonfhir.android.data.local.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.latticeonfhir.android.data.local.enums.RelationEnum
import com.latticeonfhir.android.data.local.roomdb.entities.relation.RelationEntity
import com.latticeonfhir.android.data.local.roomdb.views.RelationView

@Dao
interface RelationDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRelation(vararg relationEntity: RelationEntity): List<Long>

    @Transaction
    @Query("SELECT * FROM (SELECT * FROM RelationView WHERE patientId=:fromId AND relativeId=:toId) UNION SELECT * FROM (SELECT * FROM RelationView WHERE patientId=:toId AND relativeId=:fromId)")
    suspend fun getRelation(fromId: String, toId: String): List<RelationView>

    @Transaction
    @Query("SELECT * FROM RelationEntity WHERE fromId=:patientId")
    suspend fun getAllRelationOfPatient(patientId: String): List<RelationEntity>

    @Transaction
    @Query("DELETE FROM RelationEntity WHERE id IN (:relationIds)")
    suspend fun deleteRelation(vararg relationIds: String): Int

    @Transaction
    @Query("DELETE FROM RelationEntity WHERE fromId=:fromId AND toId=:toId")
    suspend fun deleteRelation(fromId: String, toId: String): Int

    @Transaction
    @Query("DELETE FROM RelationEntity WHERE fromId=:patientId")
    suspend fun deleteAllRelationOfPatient(patientId: String): Int

    @Transaction
    @Query("UPDATE RelationEntity SET relation=:relationEnum WHERE fromId=:fromId AND toId=:toId")
    suspend fun updateRelation(relationEnum: RelationEnum, fromId: String, toId: String): Int
}