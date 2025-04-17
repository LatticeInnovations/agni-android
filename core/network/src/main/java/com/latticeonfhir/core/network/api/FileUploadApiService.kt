package com.latticeonfhir.core.network.api

import com.latticeonfhir.core.base.server.BaseResponse
import com.latticeonfhir.core.data.server.model.file.request.FilesRequest
import com.latticeonfhir.android.data.server.model.file.response.FilesResponse
import com.latticeonfhir.core.model.server.file.request.FilesRequest
import com.latticeonfhir.core.model.server.file.response.FilesResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Streaming


interface FileUploadApiService {

    @Streaming
    @POST("upload/files")
    suspend fun getMultipleFiles(@Body filesRequest: FilesRequest): Response<ResponseBody>

    @Multipart
    @POST("upload/file")
    suspend fun uploadFile(@Part file: List<MultipartBody.Part>): Response<com.latticeonfhir.core.base.server.BaseResponse<FilesResponse>>
}