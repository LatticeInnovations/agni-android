package com.latticeonfhir.android.ui.labtestandmedicalrecord.photo.upload

import android.app.Application
import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toFile
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.data.local.roomdb.entities.labtestandmedrecord.photo.LabTestAndMedPhotoEntity
import com.latticeonfhir.android.base.viewmodel.BaseAndroidViewModel
import com.latticeonfhir.android.data.local.enums.AppointmentStatusEnum
import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.enums.PhotoUploadTypeEnum
import com.latticeonfhir.android.data.local.model.appointment.AppointmentResponseLocal
import com.latticeonfhir.android.data.local.model.labtest.LabTestPhotoResponseLocal
import com.latticeonfhir.android.data.local.repository.appointment.AppointmentRepository
import com.latticeonfhir.android.data.local.repository.file.DownloadedFileRepository
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.labtest.LabTestRepository
import com.latticeonfhir.android.data.local.repository.patient.lastupdated.PatientLastUpdatedRepository
import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepository
import com.latticeonfhir.android.data.local.repository.schedule.ScheduleRepository
import com.latticeonfhir.android.data.local.roomdb.entities.file.DownloadedFileEntity
import com.latticeonfhir.android.data.local.roomdb.entities.file.FileUploadEntity
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.data.server.model.prescription.photo.File
import com.latticeonfhir.android.data.server.repository.file.FileSyncRepository
import com.latticeonfhir.android.utils.builders.UUIDBuilder
import com.latticeonfhir.android.utils.common.Queries
import com.latticeonfhir.android.utils.common.Queries.updatePatientLastUpdated
import com.latticeonfhir.android.utils.converters.responseconverter.LabAndMedConverter.createGenericMap
import com.latticeonfhir.android.utils.converters.responseconverter.LabAndMedConverter.patchGenericMap
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.convertedDate
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import com.latticeonfhir.android.utils.file.BitmapUtils.compressImage
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
            var files = mutableListOf<File>()
            var labTestUuid = UUIDBuilder.generateUUID()
            var createdOn = Date()

            val labTestPhotoResponse: LabTestPhotoResponseLocal? =
                if (labTestRepository.getLabTestAndMedPhotoByAppointmentId(
                        appointmentResponseLocal!!.uuid,
                        photoviewType
                    ).isNotEmpty()
                ) {
                    labTestRepository.getLabTestAndMedPhotoByAppointmentId(
                        appointmentResponseLocal!!.uuid,
                        photoviewType
                    )[0]
                } else {
                    null
                }

            if (labTestPhotoResponse != null && labTestPhotoResponse.labTestFhirId == null) {
                // insert in generic post
                labTestUuid = labTestPhotoResponse.labTestId
                createdOn = labTestPhotoResponse.createdOn
                files = labTestPhotoResponse.labTests.toMutableList()
                updateOrInsertNewLabTestOrMedRecord(
                    imageUri,
                    files,
                    labTestUuid,
                    createdOn
                )
                appointmentRepository.updateAppointment(
                    appointmentResponseLocal!!.copy(status = AppointmentStatusEnum.IN_PROGRESS.value)
                        .also { updatedAppointmentResponse ->
                            appointmentResponseLocal = updatedAppointmentResponse
                        })

            } else if (labTestPhotoResponse?.labTestFhirId != null) {

                // insert generic patch
                val filename = imageUri.toFile().name
                labTestRepository.insertLabTestAndPhotos(
                    LabTestAndMedPhotoEntity(
                        id = filename + labTestPhotoResponse.labTestId,
                        labTestId = labTestPhotoResponse.labTestId,
                        fileName = filename,
                        note = ""
                    )
                ).also {
                    insertInFileRepositories(filename)
                    val labTestPhotoResponseLocal =
                        labTestRepository.getLabTestAndMedPhotoByAppointmentId(
                            appointmentResponseLocal!!.uuid, photoviewType
                        )[0]
                    genericRepository.insertOrUpdatePhotoLabTestAndMedPatch(
                        fhirId = labTestPhotoResponseLocal.labTestFhirId!!,
                        map = patchGenericMap(
                            dynamicKey = if (photoviewType == PhotoUploadTypeEnum.LAB_TEST.value) "diagnosticReportFhirId" else "medicalRecordFhirId",
                            dynamicKeyValue = labTestPhotoResponseLocal.labTestFhirId,
                            files = labTestPhotoResponseLocal.labTests
                        ),
                        typeEnum = if (photoviewType == PhotoUploadTypeEnum.LAB_TEST.value) GenericTypeEnum.LAB_TEST else GenericTypeEnum.MEDICAL_RECORD
                    )

                }
            } else {
                updateOrInsertNewLabTestOrMedRecord(
                    imageUri,
                    files,
                    labTestUuid,
                    createdOn
                )

                appointmentRepository.updateAppointment(
                    appointmentResponseLocal!!.copy(status = AppointmentStatusEnum.IN_PROGRESS.value)
                        .also { updatedAppointmentResponse ->
                            appointmentResponseLocal = updatedAppointmentResponse
                        })


            }
            inserted(true)
        }


    }

    private suspend fun updateOrInsertNewLabTestOrMedRecord(
        imageUri: Uri,
        files: MutableList<File>,
        prescriptionUuid: String,
        generatedOn: Date
    ) {
        // insert in db
        val filename = imageUri.toFile().name
        val listOfFiles = files.apply {
            add(File(filename, ""))
        }
        labTestRepository.insertPhotoLabTestAndMed(
            local = LabTestPhotoResponseLocal(
                patientId = patient!!.id,
                createdOn = generatedOn,
                labTestId = prescriptionUuid,
                labTests = listOfFiles,
                appointmentId = appointmentResponseLocal?.appointmentId
                    ?: appointmentResponseLocal!!.uuid
            ), type = photoviewType
        ).also {
            insertInFileRepositories(filename)
            genericRepository.insertPhotoLabTestAndMedRecord(
                map = createGenericMap(
                    dynamicKey = if (photoviewType == PhotoUploadTypeEnum.LAB_TEST.value) "diagnosticUuid" else "medicalReportUuid",
                    dynamicKeyValue = prescriptionUuid,
                    appointmentId = appointmentResponseLocal!!.appointmentId
                        ?: appointmentResponseLocal!!.uuid,
                    patientId = patient!!.fhirId ?: patient!!.id,
                    createdOn = generatedOn.convertedDate(),
                    files = listOfFiles
                ),
                patientId = patient!!.fhirId ?: patient!!.id,
                typeEnum = if (photoviewType == PhotoUploadTypeEnum.LAB_TEST.value) GenericTypeEnum.LAB_TEST else GenericTypeEnum.MEDICAL_RECORD
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