package com.latticeonfhir.android.data.server.repository.file

import com.latticeonfhir.android.data.local.roomdb.entities.file.FileUploadEntity
import com.latticeonfhir.android.data.server.model.file.request.FilesRequest
import com.latticeonfhir.android.data.server.model.file.response.FilesResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ResponseMapper
import okhttp3.ResponseBody
import retrofit2.Response

interface FileSyncRepository {
    suspend fun getMultipleFiles(filesRequest: FilesRequest): Response<ResponseBody>
    suspend fun startDownload()
    suspend fun uploadFile(): ResponseMapper<FilesResponse>
    suspend fun insertFile(fileUploadEntity: FileUploadEntity): List<Long>
}