package com.latticeonfhir.android.data.local.roomdb.entities.file

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "downloaded_file")
data class DownloadedFileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String
)