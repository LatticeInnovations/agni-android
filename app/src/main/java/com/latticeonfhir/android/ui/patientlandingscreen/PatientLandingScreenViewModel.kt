package com.latticeonfhir.android.ui.patientlandingscreen

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import com.latticeonfhir.android.FhirApp
import com.latticeonfhir.android.base.viewmodel.BaseAndroidViewModel
import com.latticeonfhir.android.data.local.enums.AppointmentStatusEnum
import com.latticeonfhir.android.data.local.model.appointment.AppointmentResponseLocal
import com.latticeonfhir.android.data.local.repository.appointment.AppointmentRepository
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.patient.PatientRepository
import com.latticeonfhir.android.data.local.repository.patient.lastupdated.PatientLastUpdatedRepository
import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepository
import com.latticeonfhir.android.data.local.repository.prescription.PrescriptionRepository
import com.latticeonfhir.android.data.local.repository.schedule.ScheduleRepository
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.service.workmanager.utils.Sync
import com.latticeonfhir.android.service.workmanager.workers.trigger.TriggerWorkerPeriodicImpl
import com.latticeonfhir.android.utils.common.Queries
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import com.latticeonfhir.android.utils.network.CheckNetwork
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class PatientLandingScreenViewModel @Inject constructor(
    application: Application,
    private val patientRepository: PatientRepository,
    private val appointmentRepository: AppointmentRepository,
    private val prescriptionRepository: PrescriptionRepository,
    private val genericRepository: GenericRepository,
    private val preferenceRepository: PreferenceRepository,
    private val scheduleRepository: ScheduleRepository,
    private val patientLastUpdatedRepository: PatientLastUpdatedRepository
) : BaseAndroidViewModel(application) {
    var isLaunched by mutableStateOf(false)
    var patient by mutableStateOf<PatientResponse?>(null)

    private var logoutUser by mutableStateOf(false)
    private var logoutReason by mutableStateOf("")

    var appointmentsCount by mutableIntStateOf(0)
    var uploadsCount by mutableIntStateOf(0)
    var pastAppointmentsCount by mutableIntStateOf(0)
    var isFabSelected by mutableStateOf(false)
    var showAllSlotsBookedDialog by mutableStateOf(false)

    var appointment by mutableStateOf<AppointmentResponseLocal?>(null)
    var canAddAssessment by mutableStateOf(false)
    var showAddToQueueDialog by mutableStateOf(false)
    var ifAllSlotsBooked by mutableStateOf(false)
    private val maxNumberOfAppointmentsInADay = 250

    var selectedIndex by mutableIntStateOf(0)

    private val syncService by lazy { getApplication<FhirApp>().syncService }

    private suspend fun syncData() {
        Sync.getWorkerInfo<TriggerWorkerPeriodicImpl>(getApplication<FhirApp>().applicationContext)
            .collectLatest { workInfo ->
                if (workInfo != null && workInfo.state == WorkInfo.State.ENQUEUED) {
                    getApplication<FhirApp>().launchSyncing()
                }
            }
    }

    internal fun downloadPrescriptions(patientFhirId: String) {
        if (CheckNetwork.isInternetAvailable(getApplication<FhirApp>().applicationContext)) {
            viewModelScope.launch(Dispatchers.IO) {
                syncService.patchPrescription { isErrorReceived, errorMsg ->
                    if (isErrorReceived) {
                        logoutUser = true
                        logoutReason = errorMsg
                    }
                }
                syncService.downloadPrescription(patientFhirId) { isErrorReceived, errorMsg ->
                    if (isErrorReceived) {
                        logoutUser = true
                        logoutReason = errorMsg
                    }
                }
                getUploadsCount(patient!!.id)
                syncData()
            }
        }
    }

    internal suspend fun getPatientData(id: String): PatientResponse {
        return patientRepository.getPatientById(id)[0]
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
                canAddAssessment =
                    appointmentResponse?.status == AppointmentStatusEnum.ARRIVED.value || appointmentResponse?.status == AppointmentStatusEnum.WALK_IN.value
                            || appointmentResponse?.status == AppointmentStatusEnum.IN_PROGRESS.value
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

    internal fun getScheduledAppointmentsCount(patientId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            appointmentsCount = appointmentRepository.getAppointmentsOfPatientByStatus(
                patientId,
                AppointmentStatusEnum.SCHEDULED.value
            ).filter { appointmentResponseLocal ->
                appointmentResponseLocal.slot.start.time > Date().toTodayStartDate()
            }.size
            pastAppointmentsCount = appointmentRepository.getAppointmentsOfPatient(patientId)
                .filter { appointmentResponseLocal ->
                    appointmentResponseLocal.slot.start.time < Date().toEndOfDay() && appointmentResponseLocal.status != AppointmentStatusEnum.SCHEDULED.value
                }.size
        }
    }

    internal fun getUploadsCount(patientId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            uploadsCount = prescriptionRepository.getLastPhotoPrescription(patientId).size
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