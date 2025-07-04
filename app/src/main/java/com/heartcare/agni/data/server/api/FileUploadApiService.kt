package com.heartcare.agni.data.server.api

import com.heartcare.agni.base.server.BaseResponse
import com.heartcare.agni.data.server.model.file.request.FilesRequest
import com.heartcare.agni.data.server.model.file.response.FilesResponse
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
    suspend fun uploadFile(@Part file: List<MultipartBody.Part>): Response<BaseResponse<FilesResponse>>
}