package com.latticeonfhir.android.data.server.repository.file

import com.latticeonfhir.core.data.local.enums.GenericTypeEnum
import com.latticeonfhir.core.data.local.roomdb.entities.file.FileUploadEntity
import com.latticeonfhir.core.data.server.model.file.request.FilesRequest
import com.latticeonfhir.core.data.server.model.file.response.FilesResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ResponseMapper
import okhttp3.ResponseBody
import retrofit2.Response

interface FileSyncRepository {
    suspend fun getMultipleFiles(filesRequest: FilesRequest): Response<ResponseBody>
    suspend fun startDownload(typeEnum: GenericTypeEnum,logout: (Boolean, String) -> Unit)
    suspend fun uploadFile(): ResponseMapper<FilesResponse>
    suspend fun insertFile(fileUploadEntity: FileUploadEntity): List<Long>
}