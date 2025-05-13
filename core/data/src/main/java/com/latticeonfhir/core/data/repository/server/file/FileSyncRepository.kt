package com.latticeonfhir.core.data.repository.server.file

import com.latticeonfhir.core.database.entities.file.FileUploadEntity
import com.latticeonfhir.core.model.enums.GenericTypeEnum
import com.latticeonfhir.core.model.server.file.request.FilesRequest
import com.latticeonfhir.core.model.server.file.response.FilesResponse
import com.latticeonfhir.core.utils.converters.responsemapper.ResponseMapper
import okhttp3.ResponseBody
import retrofit2.Response

interface FileSyncRepository {
    suspend fun getMultipleFiles(filesRequest: FilesRequest): Response<ResponseBody>
    suspend fun startDownload(typeEnum: GenericTypeEnum, logout: (Boolean, String) -> Unit)
    suspend fun uploadFile(): ResponseMapper<FilesResponse>
    suspend fun insertFile(fileUploadEntity: FileUploadEntity): List<Long>
}