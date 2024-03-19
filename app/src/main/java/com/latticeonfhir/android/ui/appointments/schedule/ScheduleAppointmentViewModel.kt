package com.latticeonfhir.android.ui.appointments.schedule

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.logicalId
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.enums.AppointmentStatusEnum
import com.latticeonfhir.android.data.local.enums.AppointmentStatusFhir
import com.latticeonfhir.android.data.local.enums.ChangeTypeEnum
import com.latticeonfhir.android.data.local.model.appointment.AppointmentResponseLocal
import com.latticeonfhir.android.data.local.model.patch.ChangeRequest
import com.latticeonfhir.android.data.local.repository.appointment.AppointmentRepository
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepository
import com.latticeonfhir.android.data.local.repository.schedule.ScheduleRepository
import com.latticeonfhir.android.data.server.model.scheduleandappointment.Slot
import com.latticeonfhir.android.data.server.model.scheduleandappointment.appointment.AppointmentResponse
import com.latticeonfhir.android.data.server.model.scheduleandappointment.schedule.ScheduleResponse
import com.latticeonfhir.android.utils.builders.UUIDBuilder
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.to30MinutesAfter
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.to5MinutesAfter
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toCurrentTimeInMillis
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toWeekList
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.tomorrow
import com.latticeonfhir.android.utils.fhirengine.FhirQueries.createAppointmentResource
import com.latticeonfhir.android.utils.fhirengine.FhirQueries.createEncounterResource
import com.latticeonfhir.android.utils.fhirengine.FhirQueries.createScheduleResource
import com.latticeonfhir.android.utils.fhirengine.FhirQueries.createSlotResource
import com.latticeonfhir.android.utils.fhirengine.FhirQueries.getNumberOfSlotsByScheduleId
import com.latticeonfhir.android.utils.fhirengine.FhirQueries.getScheduleByTime
import com.latticeonfhir.android.utils.fhirengine.FhirQueries.getTodayScheduledAppointmentOfPatient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Appointment
import org.hl7.fhir.r4.model.Patient
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ScheduleAppointmentViewModel @Inject constructor(
    private val fhirEngine: FhirEngine,
    private val scheduleRepository: ScheduleRepository,
    private val appointmentRepository: AppointmentRepository,
    private val preferenceRepository: PreferenceRepository,
    private val genericRepository: GenericRepository
) : BaseViewModel() {
    var isLaunched by mutableStateOf(false)
    var showDatePicker by mutableStateOf(false)
    var selectedDate by mutableStateOf(Date().tomorrow())
    var weekList by mutableStateOf(selectedDate.toWeekList())
    var selectedSlot by mutableStateOf("")
    var patient by mutableStateOf(Patient())
    var ifRescheduling by mutableStateOf(false)
    var appointment by mutableStateOf<AppointmentResponseLocal?>(null)

    internal fun getBookedSlotsCount(
        startTime: Long,
        endTime: Long,
        slotsCount: (Int) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val scheduleResource = getScheduleByTime(
                fhirEngine,
                Date(startTime),
                Date(endTime)
            )
            if (scheduleResource == null) slotsCount(0)
            else {
                slotsCount(
                    getNumberOfSlotsByScheduleId(
                        fhirEngine,
                        scheduleResource.logicalId
                    )
                )
            }
        }
    }

    internal fun insertScheduleAndAppointment(appointmentCreated: (Any) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            // check if appointment already exists for that date
            // if exists, reschedule that appointment
            // else create new appointment
            val existingAppointment = getTodayScheduledAppointmentOfPatient(
                fhirEngine,
                patient.logicalId,
                Date(selectedDate.toTodayStartDate()),
                Date(selectedDate.toEndOfDay())
            )
            if (existingAppointment != null) {
                // appointment already exists for that day
                appointmentCreated(false)
            } else {
                // create appointment
                var scheduleId = UUIDBuilder.generateUUID()
                val startTime = Date(
                    selectedSlot.toCurrentTimeInMillis(
                        selectedDate
                    )
                )
                val scheduleEndTime = Date(
                    selectedSlot.to30MinutesAfter(
                        selectedDate
                    )
                )
                val slotEndTime = Date(
                    selectedSlot.to5MinutesAfter(
                        selectedDate
                    )
                )
                val scheduleResource = getScheduleByTime(
                    fhirEngine,
                    startTime,
                    scheduleEndTime
                )
                if (scheduleResource != null) {
                    scheduleId = scheduleResource.logicalId
                } else {
                    // create a schedule
                    createScheduleResource(
                        fhirEngine,
                        scheduleId,
                        preferenceRepository.getLocationFhirId(),
                        startTime,
                        scheduleEndTime
                    )
                }
                val slotId = UUIDBuilder.generateUUID()
                val appointmentId = UUIDBuilder.generateUUID()
                fhirEngine.create(
                    createSlotResource(
                        slotId = slotId,
                        scheduleId = scheduleId,
                        startTime = startTime,
                        endTime = slotEndTime
                    ),
                    createAppointmentResource(
                        patientId = patient.logicalId,
                        locationId = preferenceRepository.getLocationFhirId(),
                        appointmentId = appointmentId,
                        appointmentStatus = Appointment.AppointmentStatus.PROPOSED,
                        typeOfAppointment = AppointmentStatusFhir.SCHEDULE.type,
                        startTime = startTime,
                        slotId = slotId
                    ),
                    createEncounterResource(
                        patientId = patient.logicalId,
                        encounterId = UUIDBuilder.generateUUID(),
                        appointmentId = appointmentId
                    )
                )
                appointmentCreated(true)
            }
        }
    }

    internal fun ifAnotherAppointmentExists(appointmentExists: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            appointmentRepository.getAppointmentsOfPatientByDate(
                patient!!.id,
                selectedDate.toTodayStartDate(),
                selectedDate.toEndOfDay()
            ).let { todaysAppointment ->
                if (todaysAppointment == null || todaysAppointment == appointment) appointmentExists(
                    false
                )
                else {
                    appointmentExists(true)
                }
            }
        }
    }

    internal fun rescheduleAppointment(rescheduled: (Int) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            // free the slot of previous schedule
            scheduleRepository.getScheduleByStartTime(appointment!!.scheduleId.time)
                .let { scheduleResponse ->
                    scheduleResponse?.let { previousScheduleResponse ->
                        scheduleRepository.updateSchedule(
                            previousScheduleResponse.copy(
                                bookedSlots = scheduleResponse.bookedSlots?.minus(1)
                            )
                        )
                    }
                }
            // check for new schedule
            var scheduleId = selectedSlot.toCurrentTimeInMillis(
                selectedDate
            )
            var scheduleFhirId: String? = null
            var id = UUIDBuilder.generateUUID()
            scheduleRepository.getScheduleByStartTime(
                scheduleId
            ).let { scheduleResponse ->
                // if already exists, increase booked slots count
                if (scheduleResponse != null) {
                    scheduleId = scheduleResponse.planningHorizon.start.time
                    id = scheduleRepository.getScheduleByStartTime(scheduleId)?.uuid!!
                    scheduleFhirId = scheduleResponse.scheduleId
                    updateSchedule(scheduleResponse)
                } else {
                    // create new schedule
                    createNewSchedule(id)
                }
            }.also {
                // update appointment
                val createdOn = Date()
                val slot = Slot(
                    start = Date(
                        selectedSlot.toCurrentTimeInMillis(
                            selectedDate
                        )
                    ),
                    end = Date(
                        selectedSlot.to5MinutesAfter(
                            selectedDate
                        )
                    )
                )
                rescheduled(
                    appointmentRepository.updateAppointment(
                        AppointmentResponseLocal(
                            appointmentId = appointment!!.appointmentId,
                            uuid = appointment!!.uuid,
                            scheduleId = Date(scheduleId),
                            createdOn = createdOn,
                            slot = slot,
                            orgId = appointment!!.orgId,
                            patientId = patient?.id!!,
                            status = appointment!!.status
                        )
                    ).also {
                        if (appointment?.appointmentId.isNullOrBlank()) {
                            // if fhir id is null, insert post request
                            genericRepository.insertAppointment(
                                AppointmentResponse(
                                    scheduleId = scheduleFhirId ?: id,
                                    createdOn = createdOn,
                                    slot = slot,
                                    patientFhirId = patient?.id,
                                    appointmentId = null,
                                    orgId = appointment!!.orgId,
                                    status = appointment!!.status,
                                    uuid = appointment!!.uuid
                                )
                            )
                        } else {
                            //  if fhir id is not null send patch request in generic
                            genericRepository.insertOrUpdateAppointmentPatch(
                                appointmentFhirId = appointment?.appointmentId!!,
                                map = mapOf(
                                    Pair(
                                        "status",
                                        ChangeRequest(
                                            operation = ChangeTypeEnum.REPLACE.value,
                                            value = AppointmentStatusEnum.SCHEDULED.value
                                        )
                                    ),
                                    Pair(
                                        "slot",
                                        ChangeRequest(
                                            operation = ChangeTypeEnum.REPLACE.value,
                                            value = slot
                                        )
                                    ),
                                    Pair(
                                        "scheduleId",
                                        ChangeRequest(
                                            operation = ChangeTypeEnum.REPLACE.value,
                                            value = scheduleFhirId ?: id
                                        )
                                    ),
                                    Pair(
                                        "createdOn",
                                        ChangeRequest(
                                            operation = ChangeTypeEnum.REPLACE.value,
                                            value = createdOn
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

    private suspend fun updateSchedule(scheduleResponse: ScheduleResponse) {
        scheduleRepository.updateSchedule(
            scheduleResponse.copy(
                bookedSlots = scheduleResponse.bookedSlots!! + 1
            )
        )
    }

    private suspend fun createNewSchedule(id: String) {
        scheduleRepository.insertSchedule(
            ScheduleResponse(
                uuid = id,
                scheduleId = null,
                bookedSlots = 1,
                orgId = preferenceRepository.getOrganizationFhirId(),
                planningHorizon = Slot(
                    start = Date(
                        selectedSlot.toCurrentTimeInMillis(
                            selectedDate
                        )
                    ),
                    end = Date(
                        selectedSlot.to30MinutesAfter(
                            selectedDate
                        )
                    )
                )
            )
        )
        genericRepository.insertSchedule(
            ScheduleResponse(
                uuid = id,
                scheduleId = null,
                bookedSlots = null,
                orgId = preferenceRepository.getOrganizationFhirId(),
                planningHorizon = Slot(
                    start = Date(
                        selectedSlot.toCurrentTimeInMillis(
                            selectedDate
                        )
                    ),
                    end = Date(
                        selectedSlot.to30MinutesAfter(
                            selectedDate
                        )
                    )
                )
            )
        )
    }
}