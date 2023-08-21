package com.latticeonfhir.android.ui.landingscreen

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.FhirApp
import com.latticeonfhir.android.R
import com.latticeonfhir.android.base.viewmodel.BaseAndroidViewModel
import com.latticeonfhir.android.data.local.enums.AppointmentStatusEnum
import com.latticeonfhir.android.data.local.enums.ChangeTypeEnum
import com.latticeonfhir.android.data.local.model.appointment.AppointmentResponseLocal
import com.latticeonfhir.android.data.local.model.patch.ChangeRequest
import com.latticeonfhir.android.data.local.repository.appointment.AppointmentRepository
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.patient.PatientRepository
import com.latticeonfhir.android.data.local.repository.prescription.PrescriptionRepository
import com.latticeonfhir.android.data.local.repository.schedule.ScheduleRepository
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.data.server.model.scheduleandappointment.appointment.AppointmentResponse
import com.latticeonfhir.android.service.sync.SyncService
import com.latticeonfhir.android.service.workmanager.request.WorkRequestBuilders
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.to14DaysWeek
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
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
    private val genericRepository: GenericRepository
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
    var selectedChip by mutableStateOf(R.string.total_appointment)
    var rescheduled by mutableStateOf(false)

    private val syncService by lazy { getApplication<FhirApp>().getSyncService() }

    internal suspend fun syncData(ioDispatcher: CoroutineDispatcher = Dispatchers.IO) {
        withContext(ioDispatcher) {
            syncService.syncLauncher { errorReceived, errorMessage ->
                getApplication<FhirApp>().sessionExpireFlow.postValue(
                    mapOf(Pair("errorReceived", errorReceived), Pair("errorMsg", errorMessage))
                )
            }
        }
    }

    internal fun getAppointmentListByDate() {
        viewModelScope.launch(Dispatchers.IO) {
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
                                patientFhirId = patientRepository.getPatientById(appointmentSelected!!.patientId)[0].fhirId,
                                appointmentId = null,
                                orgId = appointmentSelected!!.orgId,
                                status = AppointmentStatusEnum.CANCELLED.value,
                                uuid = appointmentSelected!!.uuid
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
                                patientFhirId = patientRepository.getPatientById(appointmentSelected!!.patientId)[0].fhirId,
                                appointmentId = null,
                                orgId = appointmentSelected!!.orgId,
                                status = status,
                                uuid = appointmentSelected!!.uuid
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
                }
            )
        }
    }
}
