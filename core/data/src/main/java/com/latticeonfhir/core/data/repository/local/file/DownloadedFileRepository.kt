package com.latticeonfhir.core.data.repository.local.file

import com.latticeonfhir.core.database.entities.file.DownloadedFileEntity


interface DownloadedFileRepository {
    suspend fun insertEntity(downloadedFileEntity: DownloadedFileEntity): List<Long>
    suspend fun getFileNames(): List<String>
}