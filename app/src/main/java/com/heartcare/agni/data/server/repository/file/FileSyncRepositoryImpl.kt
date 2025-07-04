package com.heartcare.agni.data.server.repository.file

import android.content.Context
import com.heartcare.agni.FhirApp
import com.heartcare.agni.data.local.enums.GenericTypeEnum
import com.heartcare.agni.data.local.enums.SyncType
import com.heartcare.agni.data.local.enums.WorkerStatus
import com.heartcare.agni.data.local.roomdb.dao.DownloadedFileDao
import com.heartcare.agni.data.local.roomdb.dao.FileUploadDao
import com.heartcare.agni.data.local.roomdb.dao.GenericDao
import com.heartcare.agni.data.local.roomdb.entities.file.DownloadedFileEntity
import com.heartcare.agni.data.local.roomdb.entities.file.FileUploadEntity
import com.heartcare.agni.data.local.roomdb.entities.generic.GenericEntity
import com.heartcare.agni.data.server.api.FileUploadApiService
import com.heartcare.agni.data.server.model.file.request.FilesRequest
import com.heartcare.agni.data.server.model.file.response.FilesResponse
import com.heartcare.agni.utils.constants.ApiConstants.FILES
import com.heartcare.agni.utils.converters.responseconverter.toListOfId
import com.heartcare.agni.utils.converters.server.responsemapper.ApiEmptyResponse
import com.heartcare.agni.utils.converters.server.responsemapper.ApiEndResponse
import com.heartcare.agni.utils.converters.server.responsemapper.ApiResponseConverter
import com.heartcare.agni.utils.converters.server.responsemapper.ResponseMapper
import com.heartcare.agni.utils.file.FileManager
import com.heartcare.agni.utils.file.FileUtils.deleteZipFile
import com.heartcare.agni.utils.file.FileUtils.saveFile
import com.heartcare.agni.utils.file.FileUtils.unzipFile
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.File
import java.util.UUID
import javax.inject.Inject

class FileSyncRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fileUploadApiService: FileUploadApiService,
    private val fileUploadDao: FileUploadDao,
    private val downloadedFileDao: DownloadedFileDao,
    private val genericDao: GenericDao
) : FileSyncRepository {

    override suspend fun getMultipleFiles(filesRequest: FilesRequest): Response<ResponseBody> {
        return fileUploadApiService.getMultipleFiles(filesRequest)
    }

    override suspend fun startDownload(
        typeEnum: GenericTypeEnum,
        logout: (Boolean, String) -> Unit
    ) {
        (context as FhirApp).photosWorkerStatus.postValue(WorkerStatus.IN_PROGRESS)
        genericDao.getSameTypeGenericEntityPayload(
            typeEnum,
            SyncType.POST
        ).let { listOfGenericEntity ->
            if (listOfGenericEntity.isNotEmpty()) {
                processDownload(
                    typeEnum,
                    listOfGenericEntity,
                    logout
                )
            } else {
                context.photosWorkerStatus.postValue(WorkerStatus.SUCCESS)
            }
        }
    }

    private suspend fun processDownload(
        typeEnum: GenericTypeEnum,
        listOfGenericEntity: List<GenericEntity>,
        logout: (Boolean, String) -> Unit
    ) {
        // delete payload if already downloaded
        val entitiesToBeDeleted =
            listOfGenericEntity.filter {
                downloadedFileDao.getDownloadedFileNames().contains(it.payload)
            }
        genericDao.deleteSyncPayload(entitiesToBeDeleted.toListOfId())

        // download rest of the files
        val filesEntitiesToBeDownloaded =
            listOfGenericEntity.filter {
                !downloadedFileDao.getDownloadedFileNames().contains(it.payload)
            }
        for (chunk in filesEntitiesToBeDownloaded.map { it.payload }.toSet().chunked(10)) {
            downloadAndSaveFiles(typeEnum, filesEntitiesToBeDownloaded, chunk, logout)
        }
    }

    private suspend fun downloadAndSaveFiles(
        typeEnum: GenericTypeEnum,
        listOfGenericEntity: List<GenericEntity>,
        filesToBeDownloaded: List<String>,
        logout: (Boolean, String) -> Unit
    ) {
        getMultipleFiles(FilesRequest(filesToBeDownloaded)).let {
            it.body()?.let { body ->
                saveAndUnzipFile(context, body)
                genericDao.deleteSyncPayload(listOfGenericEntity.toListOfId())
                downloadedFileDao.insertFile(*filesToBeDownloaded.map { uniqueFileName ->
                    DownloadedFileEntity(name = uniqueFileName)
                }.toTypedArray())
                startDownload(typeEnum, logout)
            }
            it.errorBody()?.let { errorBody ->
                (context as FhirApp).photosWorkerStatus.postValue(WorkerStatus.FAILED)
                logout(false, errorBody.string())
            }
        }
    }

    override suspend fun uploadFile(): ResponseMapper<FilesResponse> {
        val listOfFiles = fileUploadDao.getFiles().map { fileUploadEntity ->
            val file = File(FileManager.createFolder(context), fileUploadEntity.name)
            val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            MultipartBody.Part.createFormData(FILES, file.name, requestFile)
        }

        return if (listOfFiles.isEmpty()) ApiEmptyResponse()
        else {
            ApiResponseConverter.convert(
                fileUploadApiService.uploadFile(listOfFiles)
            ).run {
                when (this) {
                    is ApiEndResponse -> {
                        fileUploadDao.deleteFile(*body.files.map { file -> file.originalName }
                            .toTypedArray()).let { deletedRows ->
                            if (deletedRows > 0) uploadFile() else this
                        }
                    }

                    else -> {
                        this
                    }
                }
            }
        }
    }

    override suspend fun insertFile(fileUploadEntity: FileUploadEntity): List<Long> {
        return fileUploadDao.insertFile(fileUploadEntity)
    }

    private fun saveAndUnzipFile(context: Context, file: ResponseBody) {
        val destinationFile =
            File(FileManager.createFolder(context), UUID.randomUUID().toString() + ".zip")
        file.saveFile(destinationFile)
        unzipFile(destinationFile)
        deleteZipFile(destinationFile)
    }
}