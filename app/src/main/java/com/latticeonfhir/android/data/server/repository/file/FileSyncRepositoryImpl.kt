package com.latticeonfhir.android.data.server.repository.file

import android.content.Context
import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.enums.SyncType
import com.latticeonfhir.android.data.local.roomdb.dao.DownloadedFileDao
import com.latticeonfhir.android.data.local.roomdb.dao.FileUploadDao
import com.latticeonfhir.android.data.local.roomdb.dao.GenericDao
import com.latticeonfhir.android.data.local.roomdb.entities.file.DownloadedFileEntity
import com.latticeonfhir.android.data.local.roomdb.entities.file.FileUploadEntity
import com.latticeonfhir.android.data.local.roomdb.entities.generic.GenericEntity
import com.latticeonfhir.android.data.server.api.FileUploadApiService
import com.latticeonfhir.android.data.server.model.file.request.FilesRequest
import com.latticeonfhir.android.data.server.model.file.response.FilesResponse
import com.latticeonfhir.android.utils.constants.ApiConstants.FILES
import com.latticeonfhir.android.utils.converters.responseconverter.toListOfId
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEmptyResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEndResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiResponseConverter
import com.latticeonfhir.android.utils.converters.server.responsemapper.ResponseMapper
import com.latticeonfhir.android.utils.file.FileManager
import com.latticeonfhir.android.utils.file.FileUtils.deleteZipFile
import com.latticeonfhir.android.utils.file.FileUtils.saveFile
import com.latticeonfhir.android.utils.file.FileUtils.unzipFile
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import timber.log.Timber
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

    override suspend fun startDownload() {
        genericDao.getSameTypeGenericEntityPayload(
            GenericTypeEnum.PRESCRIPTION_PHOTO,
            SyncType.POST
        ).let { listOfGenericEntity ->
            if (listOfGenericEntity.isNotEmpty()) {
                Timber.d("manseeyy download file api called")
                processDownload(
                    listOfGenericEntity
                )
            }
        }
    }

    private suspend fun processDownload(listOfGenericEntity: List<GenericEntity>) {
        val filesToBeDownloaded =
            listOfGenericEntity.map {
                it.payload
            }.filter { !downloadedFileDao.getDownloadedFileNames().contains(it) }
        for (chunk in filesToBeDownloaded.chunked(10)) {
            downloadAndSaveFiles(listOfGenericEntity,chunk)
        }
    }

    private suspend fun downloadAndSaveFiles(listOfGenericEntity: List<GenericEntity>, filesToBeDownloaded: List<String>) {
        getMultipleFiles(FilesRequest(filesToBeDownloaded)).let {
            it.body()?.let { body ->
                saveAndUnzipFile(context, body)
                genericDao.deleteSyncPayload(listOfGenericEntity.toListOfId())
                downloadedFileDao.insertFile(*filesToBeDownloaded.map { uniqueFileName ->
                    DownloadedFileEntity(name = uniqueFileName)
                }.toTypedArray())
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