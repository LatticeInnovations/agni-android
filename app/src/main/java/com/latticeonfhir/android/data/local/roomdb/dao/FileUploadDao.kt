package com.latticeonfhir.android.data.local.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.latticeonfhir.android.data.local.roomdb.entities.file.FileUploadEntity

@Dao
interface FileUploadDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFile(vararg fileUploadEntity: FileUploadEntity): List<Long>

    @Query("SELECT * FROM fileUpload LIMIT 10")
    suspend fun getFiles(): List<FileUploadEntity>

    @Transaction
    @Query("DELETE FROM FILEUPLOAD WHERE name IN (:fileName)")
    suspend fun deleteFile(vararg fileName: String): Int
}