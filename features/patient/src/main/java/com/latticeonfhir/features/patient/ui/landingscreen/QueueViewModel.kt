package com.latticeonfhir.features.patient.ui.landingscreen

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import com.latticeonfhir.android.data.local.model.patch.ChangeRequest
import com.latticeonfhir.core.FhirApp
import com.latticeonfhir.core.base.viewmodel.BaseAndroidViewModel
import com.latticeonfhir.core.data.repository.local.appointment.AppointmentRepository
import com.latticeonfhir.core.data.repository.local.generic.GenericRepository
import com.latticeonfhir.core.data.repository.local.patient.PatientRepository
import com.latticeonfhir.core.data.repository.local.patient.lastupdated.PatientLastUpdatedRepository
import com.latticeonfhir.core.data.repository.local.schedule.ScheduleRepository
import com.latticeonfhir.core.model.enums.AppointmentStatusEnum
import com.latticeonfhir.core.model.enums.ChangeTypeEnum
import com.latticeonfhir.core.model.local.appointment.AppointmentResponseLocal
import com.latticeonfhir.core.model.server.patient.PatientLastUpdatedResponse
import com.latticeonfhir.core.model.server.patient.PatientResponse
import com.latticeonfhir.core.model.server.scheduleandappointment.appointment.AppointmentResponse
import com.latticeonfhir.core.service.workmanager.utils.Sync.getWorkerInfo
import com.latticeonfhir.core.service.workmanager.workers.trigger.TriggerWorkerPeriodicImpl
import com.latticeonfhir.core.utils.converters.TimeConverter.to14DaysWeek
import com.latticeonfhir.core.utils.converters.TimeConverter.toEndOfDay
import com.latticeonfhir.core.utils.converters.TimeConverter.toTodayStartDate
import com.latticeonfhir.features.patient.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class QueueViewModel @Inject constructor(
    application: Application,
    private val patientRepository: PatientRepository,
    private val appointmentRepository: AppointmentRepository,
    private val scheduleRepository: ScheduleRepository,
    private val genericRepository: GenericRepository,
    private val patientLastUpdatedRepository: PatientLastUpdatedRepository
) : BaseAndroidViewModel(application) {

    // queue screen
    var isLaunched by mutableStateOf(false)
    var selectedDate by mutableStateOf(Date())
    var weekList by mutableStateOf(selectedDate.to14DaysWeek())
    var showDatePicker by mutableStateOf(false)
    var appointmentsList by mutableStateOf(listOf<AppointmentResponseLocal>())
    var showCancelAppointmentDialog by mutableStateOf(false)
    var statusList by mutableStateOf(listOf<String>())
    var isSearchingInQueue by mutableStateOf(false)
    var searchQueueQuery by mutableStateOf("")
    var waitingQueueList by mutableStateOf(listOf<AppointmentResponseLocal>())
    var inProgressQueueList by mutableStateOf(listOf<AppointmentResponseLocal>())
    var scheduledQueueList by mutableStateOf(listOf<AppointmentResponseLocal>())
    var completedQueueList by mutableStateOf(listOf<AppointmentResponseLocal>())
    var cancelledQueueList by mutableStateOf(listOf<AppointmentResponseLocal>())
    var noShowQueueList by mutableStateOf(listOf<AppointmentResponseLocal>())
    var patientSelected by mutableStateOf<PatientResponse?>(null)
    var appointmentSelected by mutableStateOf<AppointmentResponseLocal?>(null)
    var selectedChip by mutableIntStateOf(R.string.total_appointment)
    var rescheduled by mutableStateOf(false)

    internal suspend fun syncData(ioDispatcher: CoroutineDispatcher = Dispatchers.IO) {
        getWorkerInfo<TriggerWorkerPeriodicImpl>(getApplication<FhirApp>().applicationContext).collectLatest { workInfo ->
            if (workInfo != null && workInfo.state == WorkInfo.State.ENQUEUED) {
                withContext(ioDispatcher) {
                    getApplication<FhirApp>().launchSyncing()
                    getAppointmentListByDate(ioDispatcher)
                }
            }
        }
    }

    internal fun getAppointmentListByDate(ioDispatcher: CoroutineDispatcher = Dispatchers.IO) {
        viewModelScope.launch(ioDispatcher) {
            appointmentsList = appointmentRepository.getAppointmentListByDate(
                selectedDate.toTodayStartDate(),
                selectedDate.toEndOfDay()
            ).filter { appointmentResponseLocal ->
                val patient = getPatientById(appointmentResponseLocal.patientId)
                patient.firstName.contains(searchQueueQuery, true)
                        || patient.middleName?.contains(searchQueueQuery, true) == true
                        || patient.lastName?.contains(searchQueueQuery, true) == true
                        || patient.fhirId?.contains(searchQueueQuery, true) == true
            }
            waitingQueueList = appointmentsList.filter { appointmentResponseLocal ->
                (appointmentResponseLocal.status == AppointmentStatusEnum.WALK_IN.value || appointmentResponseLocal.status == AppointmentStatusEnum.ARRIVED.value)
            }
            inProgressQueueList = appointmentsList.filter { appointmentResponseLocal ->
                appointmentResponseLocal.status == AppointmentStatusEnum.IN_PROGRESS.value
            }
            scheduledQueueList = appointmentsList.filter { appointmentResponseLocal ->
                appointmentResponseLocal.status == AppointmentStatusEnum.SCHEDULED.value
            }
            completedQueueList = appointmentsList.filter { appointmentResponseLocal ->
                appointmentResponseLocal.status == AppointmentStatusEnum.COMPLETED.value
            }
            cancelledQueueList = appointmentsList.filter { appointmentResponseLocal ->
                appointmentResponseLocal.status == AppointmentStatusEnum.CANCELLED.value
            }
            noShowQueueList = appointmentsList.filter { appointmentResponseLocal ->
                appointmentResponseLocal.status == AppointmentStatusEnum.NO_SHOW.value
            }
        }
    }

    internal suspend fun getPatientById(patientId: String): PatientResponse {
        return patientRepository.getPatientById(patientId)[0]
    }

    internal fun cancelAppointment(cancelled: (Int) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            cancelled(
                appointmentRepository.updateAppointment(
                    appointmentSelected!!.copy(
                        status = AppointmentStatusEnum.CANCELLED.value
                    )
                ).also {
                    scheduleRepository.getScheduleByStartTime(appointmentSelected?.scheduleId?.time!!)
                        .let { scheduleResponse ->
                            scheduleResponse?.let { previousScheduleResponse ->
                                scheduleRepository.updateSchedule(
                                    previousScheduleResponse.copy(
                                        bookedSlots = scheduleResponse.bookedSlots?.minus(1)
                                    )
                                )
                            }
                        }
                    if (appointmentSelected?.appointmentId.isNullOrBlank()) {
                        genericRepository.insertAppointment(
                            AppointmentResponse(
                                scheduleId = scheduleRepository.getScheduleByStartTime(
                                    appointmentSelected!!.scheduleId.time
                                )?.scheduleId ?: scheduleRepository.getScheduleByStartTime(
                                    appointmentSelected!!.scheduleId.time
                                )?.uuid!!,
                                createdOn = appointmentSelected!!.createdOn,
                                slot = appointmentSelected!!.slot,
                                patientFhirId = patientRepository.getPatientById(appointmentSelected!!.patientId)[0].fhirId
                                    ?: appointmentSelected!!.patientId,
                                appointmentId = null,
                                orgId = appointmentSelected!!.orgId,
                                status = AppointmentStatusEnum.CANCELLED.value,
                                uuid = appointmentSelected!!.uuid,
                                appointmentType = appointmentSelected!!.appointmentType,
                                inProgressTime = appointmentSelected!!.inProgressTime
                            )
                        )
                    } else {
                        genericRepository.insertOrUpdateAppointmentPatch(
                            appointmentFhirId = appointmentSelected?.appointmentId!!,
                            map = mapOf(
                                Pair(
                                    "status",
                                    ChangeRequest(
                                        value = AppointmentStatusEnum.CANCELLED.value,
                                        operation = ChangeTypeEnum.REPLACE.value
                                    )
                                )
                            )
                        )
                    }
                    updatePatientLastUpdated()
                }
            )
        }
    }

    internal fun updateAppointmentStatus(status: String, updated: (Int) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            updated(
                appointmentRepository.updateAppointment(
                    appointmentSelected!!.copy(
                        status = status
                    )
                ).also {
                    if (appointmentSelected?.appointmentId.isNullOrBlank()) {
                        genericRepository.insertAppointment(
                            AppointmentResponse(
                                scheduleId = scheduleRepository.getScheduleByStartTime(
                                    appointmentSelected!!.scheduleId.time
                                )?.scheduleId ?: scheduleRepository.getScheduleByStartTime(
                                    appointmentSelected!!.scheduleId.time
                                )?.uuid!!,
                                createdOn = appointmentSelected!!.createdOn,
                                slot = appointmentSelected!!.slot,
                                patientFhirId = patientRepository.getPatientById(appointmentSelected!!.patientId)[0].fhirId
                                    ?: appointmentSelected!!.patientId,
                                appointmentId = null,
                                orgId = appointmentSelected!!.orgId,
                                status = status,
                                uuid = appointmentSelected!!.uuid,
                                appointmentType = appointmentSelected!!.appointmentType,
                                inProgressTime = appointmentSelected!!.inProgressTime
                            )
                        )
                    } else {
                        genericRepository.insertOrUpdateAppointmentPatch(
                            appointmentFhirId = appointmentSelected!!.appointmentId
                                ?: appointmentSelected!!.uuid,
                            map = mapOf(
                                Pair(
                                    "status",
                                    ChangeRequest(
                                        operation = ChangeTypeEnum.REPLACE.value,
                                        value = status
                                    )
                                )
                            )
                        )
                    }
                    updatePatientLastUpdated()
                }
            )
        }
    }

    private suspend fun updatePatientLastUpdated() {
        val patientLastUpdatedResponse = PatientLastUpdatedResponse(
            uuid = appointmentSelected!!.patientId,
            timestamp = Date()
        )
        patientLastUpdatedRepository.insertPatientLastUpdatedData(patientLastUpdatedResponse)
        genericRepository.insertPatientLastUpdated(patientLastUpdatedResponse)
    }
}
