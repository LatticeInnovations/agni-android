package com.heartcare.agni.data.local.repository.file

import com.heartcare.agni.data.local.roomdb.entities.file.DownloadedFileEntity


interface DownloadedFileRepository {
    suspend fun insertEntity(downloadedFileEntity: DownloadedFileEntity): List<Long>
    suspend fun getFileNames(): List<String>
}