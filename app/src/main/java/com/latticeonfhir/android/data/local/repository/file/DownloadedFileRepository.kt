package com.latticeonfhir.core.data.local.repository.file

import com.latticeonfhir.core.data.local.roomdb.entities.file.DownloadedFileEntity


interface DownloadedFileRepository {
    suspend fun insertEntity(downloadedFileEntity: DownloadedFileEntity): List<Long>
    suspend fun getFileNames(): List<String>
}