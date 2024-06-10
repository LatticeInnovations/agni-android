package com.latticeonfhir.android.ui.prescription.photo.view

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.data.local.enums.AppointmentStatusEnum
import com.latticeonfhir.android.data.local.model.appointment.AppointmentResponseLocal
import com.latticeonfhir.android.data.local.repository.appointment.AppointmentRepository
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.patient.lastupdated.PatientLastUpdatedRepository
import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepository
import com.latticeonfhir.android.data.local.repository.prescription.PrescriptionRepository
import com.latticeonfhir.android.data.local.repository.schedule.ScheduleRepository
import com.latticeonfhir.android.data.local.roomdb.entities.prescription.photo.PrescriptionPhotoEntity
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.data.server.model.prescription.photo.File
import com.latticeonfhir.android.utils.common.Queries
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class PrescriptionPhotoViewViewModel @Inject constructor(
    private val prescriptionRepository: PrescriptionRepository,
    private val genericRepository: GenericRepository,
    private val appointmentRepository: AppointmentRepository,
    private val scheduleRepository: ScheduleRepository,
    private val patientLastUpdatedRepository: PatientLastUpdatedRepository,
    private val preferenceRepository: PreferenceRepository
) : ViewModel() {
    var isLaunched by mutableStateOf(false)
    var patient by mutableStateOf<PatientResponse?>(null)
    var isFabSelected by mutableStateOf(false)
    var showAllSlotsBookedDialog by mutableStateOf(false)
    var isLongPressed by mutableStateOf(false)
    var isTapped by mutableStateOf(false)
    var showNoteDialog by mutableStateOf(false)
    var showDeleteDialog by mutableStateOf(false)
    var displayNote by mutableStateOf(false)
    var prescriptionPhotos by mutableStateOf(listOf<File>())
    var deletedPhotos = mutableListOf<File>()
    var canAddPrescription by mutableStateOf(false)
    var showAddToQueueDialog by mutableStateOf(false)
    var ifAllSlotsBooked by mutableStateOf(false)
    var isAppointmentCompleted by mutableStateOf(false)
    var showAppointmentCompletedDialog by mutableStateOf(false)
    private val maxNumberOfAppointmentsInADay = 250
    var appointment by mutableStateOf<AppointmentResponseLocal?>(null)

    var selectedFile: File? by mutableStateOf(null)

    internal fun getPastPrescription() {
        viewModelScope.launch(Dispatchers.IO) {
            prescriptionPhotos = prescriptionRepository.getLastPhotoPrescription(patient!!.id)
            appointment = appointmentRepository.getAppointmentsOfPatientByStatus(
                patient!!.id,
                AppointmentStatusEnum.SCHEDULED.value
            ).firstOrNull { appointmentResponse ->
                appointmentResponse.slot.start.time < Date().toEndOfDay() && appointmentResponse.slot.start.time > Date().toTodayStartDate()
            }
            appointmentRepository.getAppointmentsOfPatientByDate(
                patient!!.id,
                Date().toTodayStartDate(),
                Date().toEndOfDay()
            ).let { appointmentResponse ->
                canAddPrescription =
                    appointmentResponse?.status == AppointmentStatusEnum.ARRIVED.value || appointmentResponse?.status == AppointmentStatusEnum.WALK_IN.value
                            || appointmentResponse?.status == AppointmentStatusEnum.IN_PROGRESS.value
                isAppointmentCompleted = appointmentResponse?.status == AppointmentStatusEnum.COMPLETED.value
            }
            ifAllSlotsBooked = appointmentRepository.getAppointmentListByDate(
                Date().toTodayStartDate(),
                Date().toEndOfDay()
            ).filter { appointmentResponseLocal ->
                appointmentResponseLocal.status != AppointmentStatusEnum.CANCELLED.value
            }.size >= maxNumberOfAppointmentsInADay
        }
    }

    internal fun addNoteToPrescription(
        note: String,
        added: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val dateOfFile = Date(selectedFile!!.filename.substringBefore(".").toLong())
            val prescriptionPhotoResponse = prescriptionRepository.getPrescriptionPhotoByDate(
                patient!!.id,
                dateOfFile.toTodayStartDate(),
                dateOfFile.toEndOfDay()
            )
            prescriptionRepository.insertPrescriptionPhotos(
                PrescriptionPhotoEntity(
                    id = selectedFile!!.filename + prescriptionPhotoResponse.prescriptionId,
                    prescriptionId = prescriptionPhotoResponse.prescriptionId,
                    fileName = selectedFile!!.filename,
                    note = note
                )
            )
            if (prescriptionPhotoResponse.prescriptionFhirId == null) {
                // insert generic post
                val updatedPrescriptionPhotoResponse = prescriptionRepository.getPrescriptionPhotoByDate(
                    patient!!.id,
                    dateOfFile.toTodayStartDate(),
                    dateOfFile.toEndOfDay()
                )
                genericRepository.insertPhotoPrescription(
                    updatedPrescriptionPhotoResponse
                )
            } else {
                // insert generic patch
            }
            getPastPrescription()
            added()
        }
    }

    internal fun deletePrescription(
        deleted: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val dateOfFile = Date(selectedFile!!.filename.substringBefore(".").toLong())
            val prescriptionPhotoResponse = prescriptionRepository.getPrescriptionPhotoByDate(
                patient!!.id,
                dateOfFile.toTodayStartDate(),
                dateOfFile.toEndOfDay()
            )
            // delete from local db
            prescriptionRepository.deletePrescriptionPhotos(
                PrescriptionPhotoEntity(
                    id = selectedFile!!.filename + prescriptionPhotoResponse.prescriptionId,
                    prescriptionId = prescriptionPhotoResponse.prescriptionId,
                    fileName = selectedFile!!.filename,
                    note = selectedFile!!.note
                )
            )
            // update in generic
            if (prescriptionPhotoResponse.prescriptionFhirId == null) {
                // insert generic post
                val updatedPrescriptionPhotoResponse = prescriptionRepository.getPrescriptionPhotoByDate(
                    patient!!.id,
                    dateOfFile.toTodayStartDate(),
                    dateOfFile.toEndOfDay()
                )
                genericRepository.insertPhotoPrescription(
                    updatedPrescriptionPhotoResponse
                )
            } else {
                // insert generic patch
            }
            deletedPhotos.add(selectedFile!!)
            deleted()
        }
    }

    internal fun addPatientToQueue(patient: PatientResponse, addedToQueue: (List<Long>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            Queries.addPatientToQueue(
                patient,
                scheduleRepository,
                genericRepository,
                preferenceRepository,
                appointmentRepository,
                patientLastUpdatedRepository,
                addedToQueue
            )
        }
    }

    internal fun updateStatusToArrived(
        patient: PatientResponse,
        appointment: AppointmentResponseLocal,
        updated: (Int) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            Queries.updateStatusToArrived(patient, appointment, appointmentRepository, genericRepository, preferenceRepository, scheduleRepository, patientLastUpdatedRepository, updated)
        }
    }
}