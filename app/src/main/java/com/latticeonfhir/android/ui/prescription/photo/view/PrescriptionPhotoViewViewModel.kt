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
import com.latticeonfhir.android.data.local.enums.SyncStatusMessageEnum
import com.latticeonfhir.android.data.local.enums.WorkerStatus
import com.latticeonfhir.android.data.local.model.appointment.AppointmentResponseLocal
import com.latticeonfhir.android.data.local.repository.appointment.AppointmentRepository
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.patient.lastupdated.PatientLastUpdatedRepository
import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepository
import com.latticeonfhir.android.data.local.repository.prescription.PrescriptionRepository
import com.latticeonfhir.android.data.local.repository.schedule.ScheduleRepository
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
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

    val allPrescriptionList by mutableStateOf(mutableListOf<PrescriptionFormAndPhoto>())

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
                if (!allPrescriptionList.map { it.date }
                        .contains(formPrescription.prescriptionEntity.prescriptionDate)) {
                    allPrescriptionList.add(
                        PrescriptionFormAndPhoto(
                            date = formPrescription.prescriptionEntity.prescriptionDate,
                            type = formPrescription.prescriptionEntity.prescriptionType,
                            prescription = formPrescription
                        )
                    )
                }
            }
            prescriptionRepository.getLastPhotoPrescription(patient!!.id)
                .forEach { photoPrescription ->
                    if (!allPrescriptionList.map { it.date }
                            .contains(photoPrescription.prescriptionEntity.prescriptionDate)) {
                        allPrescriptionList.add(
                            PrescriptionFormAndPhoto(
                                date = photoPrescription.prescriptionEntity.prescriptionDate,
                                type = photoPrescription.prescriptionEntity.prescriptionType,
                                prescription = photoPrescription
                            )
                        )
                    }
                }
            allPrescriptionList.sortBy {
                it.date
            }
        }
    }

    internal fun addNoteToPrescription(
        note: String,
        added: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
//            val dateOfFile = Date(selectedFile!!.filename.substringBefore(".").toLong())
//            val prescriptionPhotoResponse = prescriptionRepository.getPrescriptionPhotoByDate(
//                patient!!.id,
//                dateOfFile.toTodayStartDate(),
//                dateOfFile.toEndOfDay()
//            )
//            prescriptionRepository.insertPrescriptionPhotos(
//                PrescriptionPhotoEntity(
//                    id = selectedFile!!.filename + prescriptionPhotoResponse.prescriptionId,
//                    prescriptionId = prescriptionPhotoResponse.prescriptionId,
//                    fileName = selectedFile!!.filename,
//                    note = note
//                )
//            )
//            updateInGeneric(dateOfFile)
//            getPastPrescription()
            added()
        }
    }

    internal fun deletePrescription(
        deleted: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
//            val dateOfFile = Date(selectedFile!!.filename.substringBefore(".").toLong())
//            val prescriptionPhotoResponse = prescriptionRepository.getPrescriptionPhotoByDate(
//                patient!!.id,
//                dateOfFile.toTodayStartDate(),
//                dateOfFile.toEndOfDay()
//            )
//            // delete from local db
//            prescriptionRepository.deletePrescriptionPhotos(
//                PrescriptionPhotoEntity(
//                    id = selectedFile!!.filename + prescriptionPhotoResponse.prescriptionId,
//                    prescriptionId = prescriptionPhotoResponse.prescriptionId,
//                    fileName = selectedFile!!.filename,
//                    note = selectedFile!!.note
//                )
//            )
//            updateInGeneric(dateOfFile)
//            deletedPhotos.add(selectedFile!!)
            deleted()
        }
    }

    private suspend fun updateInGeneric(dateOfFile: Date) {
        val updatedPrescriptionPhotoResponse =
            prescriptionRepository.getPrescriptionPhotoByDate(
                patient!!.id,
                dateOfFile.toTodayStartDate(),
                dateOfFile.toEndOfDay()
            )
        if (updatedPrescriptionPhotoResponse.prescriptionFhirId == null) {
            // insert generic post
            genericRepository.insertPhotoPrescription(
                updatedPrescriptionPhotoResponse
            )
        } else {
            // insert generic patch
            genericRepository.insertOrUpdatePhotoPrescriptionPatch(
                prescriptionFhirId = updatedPrescriptionPhotoResponse.prescriptionFhirId,
                prescriptionPhotoResponse = updatedPrescriptionPhotoResponse
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