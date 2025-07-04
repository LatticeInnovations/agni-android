package com.heartcare.agni.ui.labtestandmedicalrecord.photo.upload

import android.app.Application
import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toFile
import androidx.lifecycle.viewModelScope
import com.heartcare.agni.base.viewmodel.BaseAndroidViewModel
import com.heartcare.agni.data.local.enums.AppointmentStatusEnum
import com.heartcare.agni.data.local.enums.GenericTypeEnum
import com.heartcare.agni.data.local.enums.PhotoUploadTypeEnum
import com.heartcare.agni.data.local.model.appointment.AppointmentResponseLocal
import com.heartcare.agni.data.local.model.labtest.LabTestPhotoResponseLocal
import com.heartcare.agni.data.local.repository.appointment.AppointmentRepository
import com.heartcare.agni.data.local.repository.file.DownloadedFileRepository
import com.heartcare.agni.data.local.repository.generic.GenericRepository
import com.heartcare.agni.data.local.repository.labtest.LabTestRepository
import com.heartcare.agni.data.local.repository.patient.lastupdated.PatientLastUpdatedRepository
import com.heartcare.agni.data.local.repository.preference.PreferenceRepository
import com.heartcare.agni.data.local.repository.schedule.ScheduleRepository
import com.heartcare.agni.data.local.roomdb.entities.file.DownloadedFileEntity
import com.heartcare.agni.data.local.roomdb.entities.file.FileUploadEntity
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.data.server.model.prescription.photo.File
import com.heartcare.agni.data.server.repository.file.FileSyncRepository
import com.heartcare.agni.utils.builders.UUIDBuilder
import com.heartcare.agni.utils.common.Queries
import com.heartcare.agni.utils.common.Queries.checkAndUpdateAppointmentStatusToInProgress
import com.heartcare.agni.utils.common.Queries.updatePatientLastUpdated
import com.heartcare.agni.utils.constants.LabTestAndMedConstants
import com.heartcare.agni.utils.converters.responseconverter.LabAndMedConverter.createGenericMap
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import com.heartcare.agni.utils.file.BitmapUtils.compressImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class PhotoUploadViewModel @Inject constructor(
    private val application: Application,
    private val appointmentRepository: AppointmentRepository,
    private val labTestRepository: LabTestRepository,
    private val genericRepository: GenericRepository,
    private val fileSyncRepository: FileSyncRepository,
    private val downloadedFileRepository: DownloadedFileRepository,
    private val scheduleRepository: ScheduleRepository,
    private val preferenceRepository: PreferenceRepository,
    private val patientLastUpdatedRepository: PatientLastUpdatedRepository,
) : BaseAndroidViewModel(application) {

    var patient: PatientResponse? by mutableStateOf(null)
    var isLaunched by mutableStateOf(false)
    var isSaving by mutableStateOf(false)
    var isImageCaptured by mutableStateOf(false)
    var selectedImageUri: Uri? by mutableStateOf(null)
    var isSelectedFromGallery by mutableStateOf(false)
    var tempFileName by mutableStateOf("")

    var cameraSelector by mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA)
    var flashOn by mutableStateOf(false)

    // PhotoUploadTypeEnum
    var photoviewType by mutableStateOf("")

    internal var appointmentResponseLocal: AppointmentResponseLocal? = null

    internal fun getPatientTodayAppointment(
        startDate: Date,
        endDate: Date,
        patientId: String,
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    ) {
        viewModelScope.launch(ioDispatcher) {
            appointmentResponseLocal =
                appointmentRepository.getAppointmentListByDate(startDate.time, endDate.time)
                    .firstOrNull { appointmentEntity ->
                        appointmentEntity.patientId == patientId && appointmentEntity.status != AppointmentStatusEnum.CANCELLED.value
                    }
        }
    }

    internal fun insertLabTestOrMedRecord(
        imageUri: Uri,
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
        inserted: (Boolean) -> Unit
    ) {
        viewModelScope.launch(ioDispatcher) {

            if (appointmentResponseLocal == null) {
                Queries.addPatientToQueue(
                    patient!!,
                    scheduleRepository,
                    genericRepository,
                    preferenceRepository,
                    appointmentRepository,
                    patientLastUpdatedRepository
                ) {
                    viewModelScope.launch(ioDispatcher) {
                        getPatientTodayAppointment(
                            Date(Date().toTodayStartDate()), Date(Date().toEndOfDay()), patient!!.id
                        ).also {
                            createLabTestOrMedRecord(imageUri, inserted)
                        }

                    }
                }
            } else {
                getPatientTodayAppointment(
                    Date(Date().toTodayStartDate()), Date(Date().toEndOfDay()), patient!!.id
                ).also {
                    createLabTestOrMedRecord(imageUri, inserted)
                }
            }


        }
    }

    private suspend fun createLabTestOrMedRecord(
        imageUri: Uri,
        inserted: (Boolean) -> Unit
    ) {
        if (compressImage(application, imageUri)) {
            val files = mutableListOf<File>()
            val labTestUuid = UUIDBuilder.generateUUID()
            val createdOn = Date()

            updateOrInsertNewLabTestOrMedRecord(
                imageUri,
                files,
                labTestUuid,
                createdOn
            )
            inserted(true)
        } else inserted(false)


    }

    private suspend fun updateOrInsertNewLabTestOrMedRecord(
        imageUri: Uri,
        files: MutableList<File>,
        labTestUuid: String,
        generatedOn: Date
    ) {
        // insert in db
        val filename = imageUri.toFile().name
        val listOfFiles = files.apply {
            add(File(UUIDBuilder.generateUUID(), null, filename, ""))
        }
        labTestRepository.insertPhotoLabTestAndMed(
            local = LabTestPhotoResponseLocal(
                patientId = patient!!.id,
                createdOn = generatedOn,
                labTestId = labTestUuid,
                labTests = listOfFiles,
                appointmentId = appointmentResponseLocal?.appointmentId
                    ?: appointmentResponseLocal!!.uuid
            ), type = photoviewType
        ).also {
            insertInFileRepositories(filename)
            val docIdKey=if (photoviewType == PhotoUploadTypeEnum.LAB_TEST.value) LabTestAndMedConstants.LAB_DOC_ID else LabTestAndMedConstants.MED_DOC_ID
            val fileList = files.map { file ->
                mapOf(
                    docIdKey to filename + labTestUuid,
                    LabTestAndMedConstants.FILENAME to file.filename,
                    LabTestAndMedConstants.NOTE to file.note
                )
            }
            genericRepository.insertPhotoLabTestAndMedRecord(
                map = createGenericMap(
                    dynamicKey = if (photoviewType == PhotoUploadTypeEnum.LAB_TEST.value) "diagnosticUuid" else "medicalReportUuid",
                    dynamicKeyValue = labTestUuid,
                    appointmentId = appointmentResponseLocal!!.appointmentId
                        ?: appointmentResponseLocal!!.uuid,
                    patientId = patient!!.fhirId ?: patient!!.id,
                    createdOn = generatedOn,
                    fileList = fileList
                ),
                patientId = patient!!.fhirId ?: patient!!.id,
                labTestId = labTestUuid,
                typeEnum = if (photoviewType == PhotoUploadTypeEnum.LAB_TEST.value) GenericTypeEnum.LAB_TEST else GenericTypeEnum.MEDICAL_RECORD
            )
            checkAndUpdateAppointmentStatusToInProgress(
                inProgressTime = generatedOn,
                patient = patient!!,
                appointmentResponseLocal = appointmentResponseLocal!!,
                appointmentRepository = appointmentRepository,
                scheduleRepository = scheduleRepository,
                genericRepository = genericRepository
            )
            updatePatientLastUpdated(
                patient!!.id,
                patientLastUpdatedRepository,
                genericRepository
            )
        }
    }

    private suspend fun insertInFileRepositories(filename: String) {
        fileSyncRepository.insertFile(
            FileUploadEntity(
                name = filename
            )
        )
        downloadedFileRepository.insertEntity(
            DownloadedFileEntity(
                name = filename
            )
        )
    }

}