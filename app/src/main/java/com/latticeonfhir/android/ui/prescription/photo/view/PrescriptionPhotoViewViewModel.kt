package com.latticeonfhir.android.ui.prescription.photo.view

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.FhirApp
import com.latticeonfhir.android.R
import com.latticeonfhir.android.base.viewmodel.BaseAndroidViewModel
import com.latticeonfhir.android.data.local.enums.AppointmentStatusEnum
import com.latticeonfhir.android.data.local.enums.PrescriptionType
import com.latticeonfhir.android.data.local.enums.SyncStatusMessageEnum
import com.latticeonfhir.android.data.local.enums.WorkerStatus
import com.latticeonfhir.android.data.local.model.appointment.AppointmentResponseLocal
import com.latticeonfhir.android.data.local.model.prescription.PrescriptionPhotoResponseLocal
import com.latticeonfhir.android.data.local.repository.appointment.AppointmentRepository
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.patient.lastupdated.PatientLastUpdatedRepository
import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepository
import com.latticeonfhir.android.data.local.repository.prescription.PrescriptionRepository
import com.latticeonfhir.android.data.local.repository.schedule.ScheduleRepository
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.data.server.model.prescription.photo.PrescriptionPhotoPatch
import com.latticeonfhir.android.data.server.model.prescription.photo.PrescriptionPhotoResponse
import com.latticeonfhir.android.ui.prescription.model.PrescriptionFormAndPhoto
import com.latticeonfhir.android.utils.common.Queries
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class PrescriptionPhotoViewViewModel @Inject constructor(
    application: Application,
    private val prescriptionRepository: PrescriptionRepository,
    private val genericRepository: GenericRepository,
    private val appointmentRepository: AppointmentRepository,
    private val scheduleRepository: ScheduleRepository,
    private val patientLastUpdatedRepository: PatientLastUpdatedRepository,
    private val preferenceRepository: PreferenceRepository
) : BaseAndroidViewModel(application) {
    var isLaunched by mutableStateOf(false)
    var patient by mutableStateOf<PatientResponse?>(null)
    var isFabSelected by mutableStateOf(false)
    var showAllSlotsBookedDialog by mutableStateOf(false)
    var isLongPressed by mutableStateOf(false)
    var isTapped by mutableStateOf(false)
    var showNoteDialog by mutableStateOf(false)
    var showDeleteDialog by mutableStateOf(false)
    var displayNote by mutableStateOf(true)
    var deletedPhotos = mutableListOf<PrescriptionFormAndPhoto>()
    var canAddPrescription by mutableStateOf(false)
    var showAddToQueueDialog by mutableStateOf(false)
    var ifAllSlotsBooked by mutableStateOf(false)
    var isAppointmentCompleted by mutableStateOf(false)
    var showAppointmentCompletedDialog by mutableStateOf(false)
    var showOpenSettingsDialog by mutableStateOf(false)
    var recompose by mutableStateOf(false)
    private val maxNumberOfAppointmentsInADay = 250
    var appointment by mutableStateOf<AppointmentResponseLocal?>(null)
    var showAddPrescriptionBottomSheet by mutableStateOf(false)

    var selectedFile: PrescriptionFormAndPhoto? by mutableStateOf(null)

    // syncing
    var syncStatus by mutableStateOf(WorkerStatus.TODO)

    var allPrescriptionList by mutableStateOf(mutableListOf<PrescriptionFormAndPhoto>())

    internal fun getCurrentSyncStatus() {
        viewModelScope.launch {
            getApplication<FhirApp>().syncWorkerStatus.observeForever { workerStatus ->
                syncStatus = when (workerStatus) {
                    WorkerStatus.IN_PROGRESS -> WorkerStatus.IN_PROGRESS
                    WorkerStatus.SUCCESS -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            delay(20000)
                            hideSyncStatus()
                        }
                        recompose = true
                        getPastPrescription()
                        WorkerStatus.SUCCESS
                    }

                    WorkerStatus.FAILED -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            delay(20000)
                            hideSyncStatus()
                        }
                        WorkerStatus.FAILED
                    }

                    else -> WorkerStatus.TODO
                }
            }
        }
    }

    internal fun hideSyncStatus() {
        if (syncStatus != WorkerStatus.IN_PROGRESS) syncStatus = WorkerStatus.TODO
    }

    internal fun getSyncIcon(): Int {
        return when (syncStatus) {
            WorkerStatus.IN_PROGRESS -> R.drawable.sync_icon
            WorkerStatus.SUCCESS -> R.drawable.sync_completed_icon
            WorkerStatus.FAILED -> R.drawable.sync_problem
            WorkerStatus.OFFLINE -> R.drawable.info
            else -> 0
        }
    }

    internal fun getSyncStatusMessage(): String {
        return when (syncStatus) {
            WorkerStatus.IN_PROGRESS -> SyncStatusMessageEnum.SYNCING_IN_PROGRESS.message
            WorkerStatus.SUCCESS -> SyncStatusMessageEnum.SYNCING_COMPLETED.message
            WorkerStatus.FAILED -> SyncStatusMessageEnum.SYNCING_FAILED.message
            WorkerStatus.OFFLINE -> SyncStatusMessageEnum.NO_INTERNET.message
            else -> ""
        }
    }

    internal fun getAppointmentInfo(callback: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
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
                isAppointmentCompleted =
                    appointmentResponse?.status == AppointmentStatusEnum.COMPLETED.value
            }
            ifAllSlotsBooked = appointmentRepository.getAppointmentListByDate(
                Date().toTodayStartDate(),
                Date().toEndOfDay()
            ).filter { appointmentResponseLocal ->
                appointmentResponseLocal.status != AppointmentStatusEnum.CANCELLED.value
            }.size >= maxNumberOfAppointmentsInADay
            callback()
        }
    }

    internal fun getPastPrescription() {
        viewModelScope.launch(Dispatchers.IO) {
            prescriptionRepository.getLastPrescription(patient!!.id).forEach { formPrescription ->
                allPrescriptionList.removeIf { it.date == formPrescription.generatedOn }
                allPrescriptionList.add(
                    PrescriptionFormAndPhoto(
                        date = formPrescription.generatedOn,
                        type = PrescriptionType.FORM.type,
                        prescription = formPrescription
                    )
                )
            }
            prescriptionRepository.getLastPhotoPrescription(patient!!.id)
                .forEach { photoPrescription ->
                    allPrescriptionList.removeIf { it.date == photoPrescription.generatedOn }
                    allPrescriptionList.add(
                        PrescriptionFormAndPhoto(
                            date = photoPrescription.generatedOn,
                            type = PrescriptionType.PHOTO.type,
                            prescription = photoPrescription
                        )
                    )
                }
            allPrescriptionList.sortBy {
                it.date
            }
            recompose = !recompose
        }
    }

    internal fun addNoteToPrescription(
        note: String,
        added: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            prescriptionRepository.insertPrescriptionPhotos(
                (selectedFile!!.prescription as PrescriptionPhotoResponseLocal).copy(
                    prescription = listOf(
                        (selectedFile!!.prescription as PrescriptionPhotoResponseLocal).prescription[0].copy(
                            note = note
                        )
                    )
                )
            )
            updateInGeneric((selectedFile!!.prescription as PrescriptionPhotoResponseLocal).prescriptionId)
            getPastPrescription()
            added()
        }
    }

    internal fun deletePrescription(
        deleted: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            // delete from local db
            val prescription = (selectedFile!!.prescription as PrescriptionPhotoResponseLocal)
            prescriptionRepository.deletePhotoPrescription(
                prescription
            )
            // update in generic
            deletedPhotos.add(selectedFile!!)
            deleted()
        }
    }

    private suspend fun updateInGeneric(prescriptionId: String) {
        val updatedPrescriptionPhotoResponseLocal =
            prescriptionRepository.getPrescriptionPhotoById(prescriptionId)
        if (updatedPrescriptionPhotoResponseLocal.prescriptionFhirId == null) {
            // insert generic post
            val appointmentLocal = appointmentRepository.getAppointmentsOfPatientByDate(
                patient!!.id,
                Date().toTodayStartDate(),
                Date().toEndOfDay()
            )
            genericRepository.insertPhotoPrescription(
                PrescriptionPhotoResponse(
                    appointmentUuid = updatedPrescriptionPhotoResponseLocal.appointmentId,
                    appointmentId = appointmentLocal!!.appointmentId ?: appointmentLocal.uuid,
                    generatedOn = updatedPrescriptionPhotoResponseLocal.generatedOn,
                    patientFhirId = patient!!.fhirId ?: patient!!.id,
                    prescriptionFhirId = null,
                    prescriptionId = updatedPrescriptionPhotoResponseLocal.prescriptionId,
                    prescription = updatedPrescriptionPhotoResponseLocal.prescription
                )
            )
        } else {
            // insert generic patch
            genericRepository.insertOrUpdatePhotoPrescriptionPatch(
                prescriptionFhirId = updatedPrescriptionPhotoResponseLocal.prescriptionFhirId,
                prescriptionPhotoPatch = PrescriptionPhotoPatch(
                    documentFhirId = updatedPrescriptionPhotoResponseLocal.prescriptionFhirId,
                    note = updatedPrescriptionPhotoResponseLocal.prescription[0].note,
                    filename = updatedPrescriptionPhotoResponseLocal.prescription[0].filename
                )
            )
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
            Queries.updateStatusToArrived(
                patient,
                appointment,
                appointmentRepository,
                genericRepository,
                preferenceRepository,
                scheduleRepository,
                patientLastUpdatedRepository,
                updated
            )
        }
    }
}