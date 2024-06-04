package com.latticeonfhir.android.data.local.roomdb.entities.file

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "fileUpload")
data class FileUploadEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String
)