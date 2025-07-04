package com.heartcare.agni.data.server.repository.file

import com.heartcare.agni.data.local.enums.GenericTypeEnum
import com.heartcare.agni.data.local.roomdb.entities.file.FileUploadEntity
import com.heartcare.agni.data.server.model.file.request.FilesRequest
import com.heartcare.agni.data.server.model.file.response.FilesResponse
import com.heartcare.agni.utils.converters.server.responsemapper.ResponseMapper
import okhttp3.ResponseBody
import retrofit2.Response

interface FileSyncRepository {
    suspend fun getMultipleFiles(filesRequest: FilesRequest): Response<ResponseBody>
    suspend fun startDownload(typeEnum: GenericTypeEnum,logout: (Boolean, String) -> Unit)
    suspend fun uploadFile(): ResponseMapper<FilesResponse>
    suspend fun insertFile(fileUploadEntity: FileUploadEntity): List<Long>
}