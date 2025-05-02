package com.latticeonfhir.features.prescription.ui.photo.upload

import android.app.Application
import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toFile
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.base.viewmodel.BaseAndroidViewModel
import com.latticeonfhir.core.data.local.enums.AppointmentStatusEnum
import com.latticeonfhir.core.data.local.model.appointment.AppointmentResponseLocal
import com.latticeonfhir.core.data.local.model.prescription.PrescriptionPhotoResponseLocal
import com.latticeonfhir.android.data.local.repository.appointment.AppointmentRepository
import com.latticeonfhir.android.data.local.repository.file.DownloadedFileRepository
import com.latticeonfhir.core.data.local.repository.generic.GenericRepository
import com.latticeonfhir.core.data.local.repository.patient.lastupdated.PatientLastUpdatedRepository
import com.latticeonfhir.android.data.local.repository.prescription.PrescriptionRepository
import com.latticeonfhir.core.data.local.repository.schedule.ScheduleRepository
import com.latticeonfhir.core.data.local.roomdb.entities.file.DownloadedFileEntity
import com.latticeonfhir.android.data.local.roomdb.entities.file.FileUploadEntity
import com.latticeonfhir.core.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.data.server.model.prescription.photo.File
import com.latticeonfhir.core.data.server.model.prescription.photo.PrescriptionPhotoResponse
import com.latticeonfhir.core.data.server.repository.file.FileSyncRepository
import com.latticeonfhir.core.utils.builders.UUIDBuilder
import com.latticeonfhir.core.utils.common.Queries.checkAndUpdateAppointmentStatusToInProgress
import com.latticeonfhir.core.utils.common.Queries.updatePatientLastUpdated
import com.latticeonfhir.android.utils.file.BitmapUtils.compressImage
import com.latticeonfhir.core.model.enums.AppointmentStatusEnum
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class PrescriptionPhotoUploadViewModel @Inject constructor(
    private val application: Application,
    private val appointmentRepository: AppointmentRepository,
    private val prescriptionRepository: PrescriptionRepository,
    private val genericRepository: GenericRepository,
    private val fileSyncRepository: FileSyncRepository,
    private val downloadedFileRepository: DownloadedFileRepository,
    private val scheduleRepository: ScheduleRepository,
    private val patientLastUpdatedRepository: PatientLastUpdatedRepository
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

    internal fun insertPrescription(
        imageUri: Uri,
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
        inserted: (Boolean) -> Unit
    ) {
        viewModelScope.launch(ioDispatcher) {
            // if appointment is in-progress, fetch prescription entity
            // compress and save the image
            if (compressImage(application, imageUri)) {
                insertNewPhotoPrescription(imageUri)
                inserted(true)
            } else inserted(false)
        }
    }

    private suspend fun insertNewPhotoPrescription(
        imageUri: Uri
    ) {
        // insert in db
        val filename = imageUri.toFile().name
        val listOfFiles = listOf(File(documentFhirId = null, documentUuid = UUIDBuilder.generateUUID(), filename = filename, note = ""))
        val generatedOn = Date(imageUri.toFile().name.substringBefore(".").toLong())
        val prescriptionUuid = UUIDBuilder.generateUUID()
        prescriptionRepository.insertPhotoPrescription(
            PrescriptionPhotoResponseLocal(
                patientId = patient!!.id,
                patientFhirId = patient?.fhirId,
                generatedOn = generatedOn,
                prescriptionId = prescriptionUuid,
                prescription = listOfFiles,
                appointmentId = appointmentResponseLocal!!.uuid,
                prescriptionFhirId = null
            )
        ).also {
            insertInFileRepositories(filename)
            genericRepository.insertPhotoPrescription(
                PrescriptionPhotoResponse(
                    patientFhirId = patient!!.fhirId ?: patient!!.id,
                    generatedOn = generatedOn,
                    prescriptionId = prescriptionUuid,
                    prescription = listOfFiles,
                    prescriptionFhirId = null,
                    appointmentUuid = appointmentResponseLocal!!.uuid,
                    appointmentId = appointmentResponseLocal!!.appointmentId
                        ?: appointmentResponseLocal!!.uuid,
                    status = null
                )
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