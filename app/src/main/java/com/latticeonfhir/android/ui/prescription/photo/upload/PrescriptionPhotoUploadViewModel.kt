package com.latticeonfhir.android.ui.prescription.photo.upload

import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.data.local.enums.AppointmentStatusEnum
import com.latticeonfhir.android.data.local.model.appointment.AppointmentResponseLocal
import com.latticeonfhir.android.data.local.model.prescription.PrescriptionPhotoResponseLocal
import com.latticeonfhir.android.data.local.repository.appointment.AppointmentRepository
import com.latticeonfhir.android.data.local.repository.file.DownloadedFileRepository
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.prescription.PrescriptionRepository
import com.latticeonfhir.android.data.local.roomdb.entities.file.DownloadedFileEntity
import com.latticeonfhir.android.data.local.roomdb.entities.file.FileUploadEntity
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.data.server.model.prescription.photo.File
import com.latticeonfhir.android.data.server.model.prescription.photo.PrescriptionPhotoResponse
import com.latticeonfhir.android.data.server.repository.file.FileSyncRepository
import com.latticeonfhir.android.utils.builders.UUIDBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class PrescriptionPhotoUploadViewModel @Inject constructor(
    private val appointmentRepository: AppointmentRepository,
    private val prescriptionRepository: PrescriptionRepository,
    private val genericRepository: GenericRepository,
    private val fileSyncRepository: FileSyncRepository,
    private val downloadedFileRepository: DownloadedFileRepository
) : ViewModel() {

    var patient: PatientResponse? by mutableStateOf(null)
    var isLaunched by mutableStateOf(false)
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
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
        inserted: () -> Unit
    ) {
        viewModelScope.launch(ioDispatcher) {
            // if appointment is in-progress, fetch prescription entity
            var files = mutableListOf<File>()
            var prescriptionUuid = UUIDBuilder.generateUUID()
            var generatedOn = Date()
            if (appointmentResponseLocal!!.status == AppointmentStatusEnum.IN_PROGRESS.value) {
                val prescriptionPhotoResponse =
                    prescriptionRepository.getPrescriptionPhotoByAppointmentId(
                        appointmentResponseLocal!!.uuid
                    )[0]
                if (prescriptionPhotoResponse.prescriptionFhirId == null) {
                    // insert in generic post
                    prescriptionUuid = prescriptionPhotoResponse.prescriptionId
                    generatedOn = prescriptionPhotoResponse.generatedOn
                    files = prescriptionPhotoResponse.prescription.toMutableList()
                    updateOrInsertNewPrescription(files, prescriptionUuid, generatedOn)
                } else {
                    // insert generic patch
                }
            } else {
                appointmentRepository.updateAppointment(
                    appointmentResponseLocal!!.copy(status = AppointmentStatusEnum.IN_PROGRESS.value)
                        .also { updatedAppointmentResponse ->
                            appointmentResponseLocal = updatedAppointmentResponse
                        }
                )
                updateOrInsertNewPrescription(files, prescriptionUuid, generatedOn)
            }
            inserted()
        }
    }

    private suspend fun updateOrInsertNewPrescription(
        files: MutableList<File>,
        prescriptionUuid: String,
        generatedOn: Date
    ) {
        // insert in db
        val filename = selectedImageUri!!.toFile().name
        val listOfFiles = files.apply {
            add(File(filename, null))
        }
        prescriptionRepository.insertPhotoPrescription(
            PrescriptionPhotoResponseLocal(
                patientId = patient!!.id,
                patientFhirId = patient?.fhirId,
                generatedOn = generatedOn,
                prescriptionId = prescriptionUuid,
                prescription = listOfFiles,
                appointmentId = appointmentResponseLocal!!.uuid
            )
        ).also {
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
            genericRepository.insertPhotoPrescription(
                PrescriptionPhotoResponse(
                    patientFhirId = patient!!.fhirId ?: patient!!.id,
                    generatedOn = generatedOn,
                    prescriptionId = prescriptionUuid,
                    prescription = listOfFiles,
                    prescriptionFhirId = null,
                    appointmentUuid = appointmentResponseLocal!!.uuid,
                    appointmentId = appointmentResponseLocal!!.appointmentId
                        ?: appointmentResponseLocal!!.uuid
                )
            )
        }
    }
}