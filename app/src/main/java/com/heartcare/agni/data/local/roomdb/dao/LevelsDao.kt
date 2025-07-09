package com.heartcare.agni.data.local.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.heartcare.agni.data.local.roomdb.entities.levels.LevelEntity

@Dao
interface LevelsDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLevelEntity(vararg levelEntity: LevelEntity): List<Long>

    @Query("SELECT * FROM LevelEntity WHERE levelType = :levelType AND (:precedingId IS NULL OR precedingLevelId = :precedingId) ORDER BY name")
    suspend fun getLevels(levelType: String, precedingId: String? = null): List<LevelEntity>
}