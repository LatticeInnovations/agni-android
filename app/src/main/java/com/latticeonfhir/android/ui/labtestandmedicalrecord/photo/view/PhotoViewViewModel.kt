package com.latticeonfhir.android.ui.labtestandmedicalrecord.photo.view

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.core.FhirApp
import com.latticeonfhir.android.R
import com.latticeonfhir.core.data.local.enums.AppointmentStatusEnum
import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.core.data.local.enums.PhotoUploadTypeEnum
import com.latticeonfhir.core.data.local.enums.SyncStatusMessageEnum
import com.latticeonfhir.core.data.local.enums.SyncType
import com.latticeonfhir.core.data.local.enums.WorkerStatus
import com.latticeonfhir.core.data.local.model.appointment.AppointmentResponseLocal
import com.latticeonfhir.core.model.local.labtest.LabTestPhotoResponseLocal
import com.latticeonfhir.core.data.local.repository.appointment.AppointmentRepository
import com.latticeonfhir.core.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.labtest.LabTestRepository
import com.latticeonfhir.core.data.local.repository.patient.lastupdated.PatientLastUpdatedRepository
import com.latticeonfhir.core.data.local.repository.preference.PreferenceRepository
import com.latticeonfhir.android.data.local.repository.schedule.ScheduleRepository
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.core.data.server.model.prescription.photo.File
import com.latticeonfhir.core.utils.common.Queries
import com.latticeonfhir.core.utils.common.Queries.updatePatientLastUpdated
import com.latticeonfhir.core.utils.constants.LabTestAndMedConstants
import com.latticeonfhir.android.utils.converters.responseconverter.LabAndMedConverter.createGenericMap
import com.latticeonfhir.android.utils.converters.responseconverter.LabAndMedConverter.patchGenericMap
import com.latticeonfhir.core.base.viewmodel.BaseAndroidViewModel
import com.latticeonfhir.core.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.latticeonfhir.core.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class PhotoViewViewModel @Inject constructor(
    application: Application,
    private val labTestRepository: LabTestRepository,
    private val genericRepository: GenericRepository,
    private val appointmentRepository: AppointmentRepository,
    private val patientLastUpdatedRepository: PatientLastUpdatedRepository,
    private val preferenceRepository: PreferenceRepository,
    private val scheduleRepository: ScheduleRepository,
) : BaseAndroidViewModel(application) {
    var isLaunched by mutableStateOf(false)
    var patient by mutableStateOf<PatientResponse?>(null)
    var isFabSelected by mutableStateOf(false)
    var isLongPressed by mutableStateOf(false)
    var isTapped by mutableStateOf(false)
    var showNoteDialog by mutableStateOf(false)
    var showDeleteDialog by mutableStateOf(false)
    var displayNote by mutableStateOf(true)
    var labTestPhotos by mutableStateOf(listOf<File>())
    var deletedPhotos = mutableListOf<File>()
    private var canAddLabTest by mutableStateOf(false)
    var showAddToQueueDialog by mutableStateOf(false)
    var isAppointmentCompleted by mutableStateOf(false)
    var showAppointmentCompletedDialog by mutableStateOf(false)
    var showOpenSettingsDialog by mutableStateOf(false)
    var recompose by mutableStateOf(false)
    var appointment by mutableStateOf<AppointmentResponseLocal?>(null)

    var selectedFile: File? by mutableStateOf(null)

    // syncing
    var syncStatus by mutableStateOf(WorkerStatus.TODO)

    // PhotoUploadTypeEnum
    var photoviewType by mutableStateOf("")

    var canAddAssessment by mutableStateOf(false)
    var ifAllSlotsBooked by mutableStateOf(false)
    var showAllSlotsBookedDialog by mutableStateOf(false)
    private val maxNumberOfAppointmentsInADay = 250

    internal fun getAppointmentInfo(
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
        callback: () -> Unit
    ) {
        viewModelScope.launch(ioDispatcher) {
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
                canAddAssessment =
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
                        getPastLabAndMedTest()
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


    internal suspend fun getStudentTodayAppointment(
        startDate: Date, endDate: Date, patientId: String
    ) {
        appointment = appointmentRepository.getAppointmentListByDate(startDate.time, endDate.time)
            .firstOrNull { appointmentEntity ->
                appointmentEntity.patientId == patientId && appointmentEntity.status != AppointmentStatusEnum.CANCELLED.value
            }.also {
                canAddLabTest = true
                isAppointmentCompleted =
                    appointment?.status == AppointmentStatusEnum.COMPLETED.value
            }
    }

    internal fun getPastLabAndMedTest() {
        viewModelScope.launch(Dispatchers.IO) {
            labTestPhotos = labTestRepository.getLastPhotoLabAndMedTest(patient!!.id, photoviewType)
            Timber.d("LabTest: $labTestPhotos")
        }
    }

    internal fun addNoteToLabTest(
        note: String,
        added: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {

            val list = labTestRepository.getLabTestAndMedPhotoByAppointmentId(
                patientId = patient!!.id, photoviewType = photoviewType
            )

            var labTestPhotoResponseLocal: LabTestPhotoResponseLocal? = null
            list.map {
                it.labTests.map { files ->
                    if (files.filename == selectedFile!!.filename) {
                        labTestPhotoResponseLocal = it
                    }
                }
            }.also {
                labTestPhotoResponseLocal?.let {
                    labTestRepository.insertLabTestAndPhotos(
                        labTestPhotoResponseLocal!!.copy(labTests = labTestPhotoResponseLocal!!.labTests.map {
                            it.copy(
                                note = note
                            )
                        }), type = photoviewType
                    )
                    updateInGeneric(labTestPhotoResponseLocal!!.copy(labTests = labTestPhotoResponseLocal!!.labTests.map {
                        it.copy(
                            note = note
                        )
                    }))
                }
            }

            updatePatientLastUpdated(
                patient!!.id,
                patientLastUpdatedRepository,
                genericRepository
            )
            getPastLabAndMedTest()
            added()
        }
    }

    internal fun deleteLabTest(
        deleted: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val list = labTestRepository.getLabTestAndMedPhotoByAppointmentId(
                patientId = patient!!.id, photoviewType = photoviewType
            )

            var labTestPhotoResponseLocal: LabTestPhotoResponseLocal? = null
            list.map {
                it.labTests.map { files ->
                    if (files.filename == selectedFile!!.filename) {
                        labTestPhotoResponseLocal = it
                    }
                }
            }.also {
                labTestPhotoResponseLocal?.let {
                    // delete from local db
                    labTestRepository.deleteLabTestAndPhotos(
                        labTestPhotoResponseLocal!!, type = photoviewType
                    )
                    // update in generic
                    if (labTestPhotoResponseLocal!!.labTestFhirId == null) {
                        // remove post request of prescription
                        genericRepository.removeGenericRecord(labTestPhotoResponseLocal!!.labTestId)
                    } else {
                        // add delete request of prescription
                        genericRepository.insertDeleteRequest(
                            fhirId = labTestPhotoResponseLocal!!.labTestFhirId!!,
                            typeEnum = if (photoviewType == PhotoUploadTypeEnum.LAB_TEST.value) GenericTypeEnum.LAB_TEST else GenericTypeEnum.MEDICAL_RECORD,
                            syncType = SyncType.DELETE
                        )
                    }
                }
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

    private suspend fun updateInGeneric(labTestPhotoResponseLocal: LabTestPhotoResponseLocal) {

        if (labTestPhotoResponseLocal.labTestFhirId == null) {
            // insert generic post
            val docIdKey =
                if (photoviewType == PhotoUploadTypeEnum.LAB_TEST.value) LabTestAndMedConstants.LAB_DOC_ID else LabTestAndMedConstants.MED_DOC_ID
            val fileList = labTestPhotoResponseLocal.labTests.map { file ->
                mapOf(
                    docIdKey to selectedFile?.filename + labTestPhotoResponseLocal.labTestId,
                    LabTestAndMedConstants.FILENAME to file.filename,
                    LabTestAndMedConstants.NOTE to file.note
                )
            }
            genericRepository.insertPhotoLabTestAndMedRecord(
                map = createGenericMap(
                    dynamicKey = if (photoviewType == PhotoUploadTypeEnum.LAB_TEST.value) "diagnosticUuid" else "medicalReportUuid",
                    dynamicKeyValue = labTestPhotoResponseLocal.labTestId,
                    appointmentId = labTestPhotoResponseLocal.appointmentId,
                    patientId = patient!!.fhirId ?: patient!!.id,
                    createdOn = labTestPhotoResponseLocal.createdOn,
                    fileList = fileList
                ),
                patientId = patient!!.fhirId ?: patient!!.id,
                labTestId = labTestPhotoResponseLocal.labTestId,
                typeEnum = if (photoviewType == PhotoUploadTypeEnum.LAB_TEST.value) GenericTypeEnum.LAB_TEST else GenericTypeEnum.MEDICAL_RECORD
            )

        } else {
            // insert generic patch
            genericRepository.insertOrUpdatePhotoLabTestAndMedPatch(
                fhirId = labTestPhotoResponseLocal.labTests[0].documentFhirId!!,
                map = patchGenericMap(
                    dynamicKeyValue = labTestPhotoResponseLocal.labTests[0].documentFhirId!!,
                    files = labTestPhotoResponseLocal.labTests
                ),
                typeEnum = if (photoviewType == PhotoUploadTypeEnum.LAB_TEST.value) GenericTypeEnum.LAB_TEST else GenericTypeEnum.MEDICAL_RECORD

            )
        }
        updatePatientLastUpdated(
            patient!!.id,
            patientLastUpdatedRepository,
            genericRepository
        )

        getPastLabAndMedTest()
    }

    internal fun addPatientToQueue(
        patient: PatientResponse,
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
        addedToQueue: (List<Long>) -> Unit
    ) {
        viewModelScope.launch(ioDispatcher) {
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
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
        updated: (Int) -> Unit
    ) {
        viewModelScope.launch(ioDispatcher) {
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