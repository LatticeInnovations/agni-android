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
import com.latticeonfhir.android.data.local.model.prescription.PrescriptionResponseLocal
import com.latticeonfhir.android.data.local.repository.appointment.AppointmentRepository
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.prescription.PrescriptionRepository
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.data.server.model.prescription.photo.Document
import com.latticeonfhir.android.data.server.model.prescription.photo.PrescriptionPhotoResponse
import com.latticeonfhir.android.data.server.model.prescription.prescriptionresponse.Medication
import com.latticeonfhir.android.data.server.model.prescription.prescriptionresponse.PrescriptionResponse
import com.latticeonfhir.android.utils.builders.UUIDBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class PrescriptionPhotoUploadViewModel @Inject constructor(
    private val appointmentRepository: AppointmentRepository,
    private val prescriptionRepository: PrescriptionRepository,
    private val genericRepository: GenericRepository
): ViewModel() {

    var patient: PatientResponse? by mutableStateOf(null)
    var isLaunched by mutableStateOf(false)
    var isImageCaptured by mutableStateOf(false)
    var selectedImageUri: Uri? by mutableStateOf(null)

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
        date: Date = Date(),
        prescriptionId: String = UUIDBuilder.generateUUID(),
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
        inserted: (Long) -> Unit
    ) {
        viewModelScope.launch {
            inserted(withContext(ioDispatcher) {
                // insert in db
                prescriptionRepository.insertPhotoPrescription(
                    PrescriptionPhotoResponseLocal(
                        patientId = patient!!.id,
                        patientFhirId = patient?.fhirId,
                        generatedOn = date,
                        prescriptionId = prescriptionId,
                        prescription = listOf(Document(selectedImageUri!!.toFile().name)),
                        appointmentId = appointmentResponseLocal!!.uuid
                    )
                ).also {
                    genericRepository.insertPhotoPrescription(
                        PrescriptionPhotoResponse(
                            patientFhirId = patient!!.fhirId ?: patient!!.id,
                            generatedOn = date,
                            prescriptionId = prescriptionId,
                            prescription = listOf(Document(selectedImageUri!!.toFile().name)),
                            prescriptionFhirId = null,
                            appointmentUuid = appointmentResponseLocal!!.uuid,
                            appointmentId = appointmentResponseLocal!!.appointmentId
                                ?: appointmentResponseLocal!!.uuid
                        )
                    )
                    appointmentRepository.updateAppointment(
                        appointmentResponseLocal!!.copy(status = AppointmentStatusEnum.IN_PROGRESS.value)
                            .also { updatedAppointmentResponse ->
                                appointmentResponseLocal = updatedAppointmentResponse
                            }
                    )
                }
            })
        }
    }
}