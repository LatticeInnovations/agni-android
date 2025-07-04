package com.heartcare.agni.data.local.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.heartcare.agni.data.local.roomdb.entities.file.DownloadedFileEntity

@Dao
interface DownloadedFileDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFile(vararg downloadedFileEntity: DownloadedFileEntity): List<Long>

    @Query("SELECT name FROM downloaded_file")
    suspend fun getDownloadedFileNames(): List<String>
}