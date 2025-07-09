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

    @Transaction
    @Query("SELECT * FROM LevelEntity")
    suspend fun getLevelEntities(): List<LevelEntity>
}