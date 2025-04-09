package com.latticeonfhir.core.data.repository.local.file

import com.latticeonfhir.core.data.local.roomdb.dao.DownloadedFileDao
import com.latticeonfhir.android.data.local.roomdb.entities.file.DownloadedFileEntity
import javax.inject.Inject

class DownloadedFileRepositoryImpl @Inject constructor(
    private val downloadedFileDao: DownloadedFileDao
) : DownloadedFileRepository {
    override suspend fun insertEntity(downloadedFileEntity: DownloadedFileEntity): List<Long> {
        return downloadedFileDao.insertFile(downloadedFileEntity)
    }

    override suspend fun getFileNames(): List<String> {
        return downloadedFileDao.getDownloadedFileNames()
    }
}