package com.latticeonfhir.android.ui.appointments

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.enums.AppointmentStatusEnum
import com.latticeonfhir.android.data.local.enums.ChangeTypeEnum
import com.latticeonfhir.android.data.local.model.patch.ChangeRequest
import com.latticeonfhir.android.data.local.repository.appointment.AppointmentRepository
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepository
import com.latticeonfhir.android.data.local.repository.schedule.ScheduleRepository
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.data.server.model.scheduleandappointment.Slot
import com.latticeonfhir.android.data.server.model.scheduleandappointment.appointment.AppointmentResponse
import com.latticeonfhir.android.data.server.model.scheduleandappointment.schedule.ScheduleResponse
import com.latticeonfhir.android.utils.builders.UUIDBuilder
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.to30MinutesAfter
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.to5MinutesAfter
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toAppointmentTime
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toCurrentTimeInMillis
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toSlotStartTime
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AppointmentsScreenViewModel @Inject constructor(
    private val appointmentRepository: AppointmentRepository,
    private val genericRepository: GenericRepository,
    private val scheduleRepository: ScheduleRepository,
    private  val preferenceRepository: PreferenceRepository
) : BaseViewModel() {
    var isLaunched by mutableStateOf(false)

    var patient by mutableStateOf<PatientResponse?>(null)

    val tabs = listOf("Upcoming", "Completed")

    var isFabSelected by mutableStateOf(false)
    var ifAlreadyWaiting by mutableStateOf(false)
    var ifAllSlotsBooked by mutableStateOf(false)
    var showAllSlotsBookedDialog by mutableStateOf(false)
    var rescheduled by mutableStateOf(false)
    var scheduled by mutableStateOf(false)

    var showCancelAppointmentDialog by mutableStateOf(false)
    var selectedAppointment by mutableStateOf<AppointmentResponse?>(null)
    var todaysAppointment by mutableStateOf<AppointmentResponse?>(null)

    var upcomingAppointmentsList by mutableStateOf(
        listOf<AppointmentResponse>()
    )

    var completedAppointmentsList by mutableStateOf(listOf<AppointmentResponse>())

    internal fun getAppointmentsList(patientId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            upcomingAppointmentsList = appointmentRepository.getAppointmentsOfPatientByStatus(
                patientId,
                AppointmentStatusEnum.SCHEDULED.value
            )
            todaysAppointment = appointmentRepository.getAppointmentsOfPatientByStatus(
                patientId,
                AppointmentStatusEnum.SCHEDULED.value
            ).firstOrNull { appointmentResponse ->
                appointmentResponse.slot.start.time < Date().toEndOfDay()
            }
            completedAppointmentsList = appointmentRepository.getAppointmentsOfPatientByStatus(
                patientId,
                AppointmentStatusEnum.COMPLETED.value
            )
            appointmentRepository.getAppointmentsOfPatientByDate(
                patientId,
                Date().toTodayStartDate(),
                Date().toEndOfDay()
            ).let { appointmentResponse ->
                ifAlreadyWaiting = if (appointmentResponse == null) false
                else appointmentResponse.status == AppointmentStatusEnum.WALK_IN.value || appointmentResponse.status == AppointmentStatusEnum.ARRIVED.value
            }
            ifAllSlotsBooked = appointmentRepository.getAppointmentListByDate(Date().toTodayStartDate(), Date().toEndOfDay()).size >= 80
        }
    }

    internal fun cancelAppointment(cancelled: (Int) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            cancelled(
                appointmentRepository.updateAppointment(
                    selectedAppointment!!.copy(
                        status = AppointmentStatusEnum.CANCELLED.value
                    )
                ).also {
                    scheduleRepository.getScheduleById(selectedAppointment?.scheduleId!!).let {  scheduleResponse ->
                        scheduleRepository.updateSchedule(
                            scheduleResponse.copy(
                                bookedSlots = scheduleResponse.bookedSlots?.minus(1)
                            )
                        )
                    }
                    genericRepository.insertOrUpdateAppointmentPatch(
                        appointmentFhirId = selectedAppointment?.appointmentId!!,
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
            )
        }
    }

    internal fun addPatientToQueue(addedToQueue: (List<Long>) -> Unit){
        viewModelScope.launch(Dispatchers.IO){
            val selectedSlot = Date().toSlotStartTime()
            Timber.d("manseeyy $selectedSlot")
            var scheduleId = UUIDBuilder.generateUUID()
            var scheduleFhirId: String? = null
            scheduleRepository.getScheduleByStartTime(
                selectedSlot.toCurrentTimeInMillis(
                    Date()
                )
            ).let { scheduleResponse ->
                if (scheduleResponse != null) {
                    Timber.d("manseeyy already scheduled")
                    scheduleId = scheduleResponse.uuid
                    scheduleFhirId = scheduleResponse.scheduleId
                    scheduleRepository.updateSchedule(
                        scheduleResponse.copy(
                            bookedSlots = scheduleResponse.bookedSlots!! + 1
                        )
                    )
                } else {
                    scheduleRepository.insertSchedule(
                        ScheduleResponse(
                            uuid = scheduleId,
                            scheduleId = null,
                            bookedSlots = 1,
                            orgId = preferenceRepository.getOrganizationFhirId(),
                            planningHorizon = Slot(
                                start = Date(
                                    selectedSlot.toCurrentTimeInMillis(
                                        Date()
                                    )
                                ),
                                end = Date(
                                    selectedSlot.to30MinutesAfter(
                                        Date()
                                    )
                                )
                            )
                        )
                    )
                    genericRepository.insertSchedule(
                        ScheduleResponse(
                            uuid = scheduleId,
                            scheduleId = null,
                            bookedSlots = null,
                            orgId = preferenceRepository.getOrganizationFhirId(),
                            planningHorizon = Slot(
                                start = Date(
                                    selectedSlot.toCurrentTimeInMillis(
                                        Date()
                                    )
                                ),
                                end = Date(
                                    selectedSlot.to30MinutesAfter(
                                        Date()
                                    )
                                )
                            )
                        )
                    )
                }
            }.also {
                val appointmentId = UUIDBuilder.generateUUID()
                val createdOn = Date()
                addedToQueue(
                    appointmentRepository.addAppointment(
                        AppointmentResponse(
                            appointmentId = null,
                            uuid = appointmentId,
                            patientFhirId = patient?.id,
                            scheduleId = scheduleId,
                            createdOn = createdOn,
                            orgId = preferenceRepository.getOrganizationFhirId(),
                            slot = Slot(
                                start = Date(Date().toAppointmentTime().toCurrentTimeInMillis(Date())),
                                end = Date(
                                    Date().toAppointmentTime().to5MinutesAfter(
                                        Date()
                                    )
                                )
                            ),
                            status = AppointmentStatusEnum.WALK_IN.value
                        )
                    ).also {
                        genericRepository.insertAppointment(
                            AppointmentResponse(
                                appointmentId = null,
                                uuid = appointmentId,
                                patientFhirId = patient?.fhirId ?: patient?.id,
                                scheduleId = scheduleFhirId ?: scheduleId,
                                createdOn = createdOn,
                                orgId = preferenceRepository.getOrganizationFhirId(),
                                slot = Slot(
                                    start = Date(Date().toAppointmentTime().toCurrentTimeInMillis(Date())),
                                    end = Date(
                                        Date().toAppointmentTime().to5MinutesAfter(
                                            Date()
                                        )
                                    )
                                ),
                                status = AppointmentStatusEnum.WALK_IN.value
                            )
                        )
                    }
                )
            }
        }
    }
    internal fun updateStatusToArrived(updated: (Int) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            updated(
                appointmentRepository.updateAppointment(
                    todaysAppointment!!.copy(
                        status = AppointmentStatusEnum.ARRIVED.value
                    )
                ).also {
                    genericRepository.insertOrUpdateAppointmentPatch(
                        appointmentFhirId = todaysAppointment!!.appointmentId
                            ?: todaysAppointment!!.uuid,
                        map = mapOf(
                            Pair(
                                "status",
                                ChangeRequest(
                                    operation = ChangeTypeEnum.REPLACE.value,
                                    value = AppointmentStatusEnum.ARRIVED.value
                                )
                            )
                        )
                    )
                }
            )
        }
    }
}