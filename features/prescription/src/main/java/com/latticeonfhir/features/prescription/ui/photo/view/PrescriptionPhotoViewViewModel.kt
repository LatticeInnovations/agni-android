package com.latticeonfhir.features.prescription.ui.photo.view

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.core.FhirApp
import com.latticeonfhir.core.base.viewmodel.BaseAndroidViewModel
import com.latticeonfhir.core.data.repository.local.appointment.AppointmentRepository
import com.latticeonfhir.core.data.repository.local.generic.GenericRepository
import com.latticeonfhir.core.data.repository.local.patient.lastupdated.PatientLastUpdatedRepository
import com.latticeonfhir.core.data.repository.local.preference.PreferenceRepository
import com.latticeonfhir.core.data.repository.local.prescription.PrescriptionRepository
import com.latticeonfhir.core.data.repository.local.schedule.ScheduleRepository
import com.latticeonfhir.core.data.utils.common.Queries
import com.latticeonfhir.core.data.utils.common.Queries.updatePatientLastUpdated
import com.latticeonfhir.core.model.enums.AppointmentStatusEnum
import com.latticeonfhir.core.model.enums.GenericTypeEnum
import com.latticeonfhir.core.model.enums.PrescriptionType
import com.latticeonfhir.core.model.enums.SyncStatusMessageEnum
import com.latticeonfhir.core.model.enums.SyncType
import com.latticeonfhir.core.model.enums.WorkerStatus
import com.latticeonfhir.core.model.local.appointment.AppointmentResponseLocal
import com.latticeonfhir.core.model.local.prescription.PrescriptionPhotoResponseLocal
import com.latticeonfhir.core.model.server.patient.PatientResponse
import com.latticeonfhir.core.model.server.prescription.photo.PrescriptionPhotoPatch
import com.latticeonfhir.core.model.server.prescription.photo.PrescriptionPhotoResponse
import com.latticeonfhir.core.utils.converters.TimeConverter.toEndOfDay
import com.latticeonfhir.core.utils.converters.TimeConverter.toTodayStartDate
import com.latticeonfhir.features.prescription.R
import com.latticeonfhir.features.prescription.data.model.PrescriptionFormAndPhoto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.concurrent.CopyOnWriteArrayList
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
    var isLoading by mutableStateOf(true)

    private var _allPrescriptionList = CopyOnWriteArrayList<PrescriptionFormAndPhoto>()
    var allPrescriptionList: List<PrescriptionFormAndPhoto> = listOf()

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
                        CoroutineScope(Dispatchers.IO).launch {
                            getPastPrescription()
                        }
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

    internal suspend fun getPastPrescription(
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    ) {
        withContext(ioDispatcher) {
            val newPrescriptions = mutableListOf<PrescriptionFormAndPhoto>()
            prescriptionRepository.getLastPrescription(patient!!.id)
                .forEach { formPrescription ->
                    newPrescriptions.add(
                        PrescriptionFormAndPhoto(
                            date = formPrescription.generatedOn,
                            type = PrescriptionType.FORM.type,
                            prescription = formPrescription
                        )
                    )
                }
            prescriptionRepository.getLastPhotoPrescription(patient!!.id)
                .forEach { photoPrescription ->
                    newPrescriptions.add(
                        PrescriptionFormAndPhoto(
                            date = photoPrescription.generatedOn,
                            type = PrescriptionType.PHOTO.type,
                            prescription = photoPrescription
                        )
                    )
                }
            _allPrescriptionList.removeIf { old ->
                newPrescriptions.any { new -> old.date == new.date }
            }
            _allPrescriptionList.addAll(newPrescriptions)
            _allPrescriptionList.sortBy { it.date }
            allPrescriptionList = _allPrescriptionList
            isLoading = false
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
            updatePatientLastUpdated(
                patient!!.id,
                patientLastUpdatedRepository,
                genericRepository
            )
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
            if (prescription.prescriptionFhirId == null) {
                // remove post request of prescription
                genericRepository.removeGenericRecord(prescription.prescriptionId)
            } else {
                // add delete request of prescription
                genericRepository.insertDeleteRequest(
                    fhirId = prescription.prescriptionFhirId!!,
                    typeEnum = GenericTypeEnum.PRESCRIPTION_PHOTO_RESPONSE,
                    syncType = SyncType.DELETE
                )
            }
            updatePatientLastUpdated(
                patient!!.id,
                patientLastUpdatedRepository,
                genericRepository
            )
            deletedPhotos.add(selectedFile!!)
            deleted()
        }
    }

    private suspend fun updateInGeneric(prescriptionId: String) {
        val updatedPrescriptionPhotoResponseLocal =
            prescriptionRepository.getPrescriptionPhotoById(prescriptionId)
        if (updatedPrescriptionPhotoResponseLocal.prescriptionFhirId == null) {
            // insert generic post
            val appointmentResponse = appointmentRepository.getAppointmentByAppointmentId(
                updatedPrescriptionPhotoResponseLocal.appointmentId
            )
            genericRepository.insertPhotoPrescription(
                PrescriptionPhotoResponse(
                    appointmentUuid = updatedPrescriptionPhotoResponseLocal.appointmentId,
                    appointmentId = appointmentResponse.appointmentId ?: appointmentResponse.uuid,
                    generatedOn = updatedPrescriptionPhotoResponseLocal.generatedOn,
                    patientFhirId = patient!!.fhirId ?: patient!!.id,
                    prescriptionFhirId = null,
                    prescriptionId = updatedPrescriptionPhotoResponseLocal.prescriptionId,
                    prescription = updatedPrescriptionPhotoResponseLocal.prescription,
                    status = null
                )
            )
        } else {
            // insert generic patch
            genericRepository.insertOrUpdatePhotoPrescriptionPatch(
                prescriptionFhirId = updatedPrescriptionPhotoResponseLocal.prescription[0].documentFhirId!!,
                prescriptionPhotoPatch = PrescriptionPhotoPatch(
                    documentFhirId = updatedPrescriptionPhotoResponseLocal.prescription[0].documentFhirId!!,
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