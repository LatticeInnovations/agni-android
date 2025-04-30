package com.latticeonfhir.features.appointment.ui.schedule

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.data.local.model.patch.ChangeRequest
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.core.data.repository.local.appointment.AppointmentRepository
import com.latticeonfhir.core.data.repository.local.generic.GenericRepository
import com.latticeonfhir.core.data.repository.local.patient.lastupdated.PatientLastUpdatedRepository
import com.latticeonfhir.core.data.repository.local.preference.PreferenceRepository
import com.latticeonfhir.core.data.repository.local.schedule.ScheduleRepository
import com.latticeonfhir.core.model.enums.AppointmentStatusEnum
import com.latticeonfhir.core.model.enums.AppointmentTypeEnum
import com.latticeonfhir.core.model.enums.ChangeTypeEnum
import com.latticeonfhir.core.model.local.appointment.AppointmentResponseLocal
import com.latticeonfhir.core.model.server.patient.PatientLastUpdatedResponse
import com.latticeonfhir.core.model.server.patient.PatientResponse
import com.latticeonfhir.core.model.server.scheduleandappointment.Slot
import com.latticeonfhir.core.model.server.scheduleandappointment.appointment.AppointmentResponse
import com.latticeonfhir.core.model.server.scheduleandappointment.schedule.ScheduleResponse
import com.latticeonfhir.core.utils.builders.UUIDBuilder
import com.latticeonfhir.core.utils.converters.TimeConverter.to30MinutesAfter
import com.latticeonfhir.core.utils.converters.TimeConverter.toCurrentTimeInMillis
import com.latticeonfhir.core.utils.converters.TimeConverter.toEndOfDay
import com.latticeonfhir.core.utils.converters.TimeConverter.toTodayStartDate
import com.latticeonfhir.core.utils.converters.TimeConverter.toWeekList
import com.latticeonfhir.core.utils.converters.TimeConverter.tomorrow
import com.latticeonfhir.core.utils.converters.TimeConverter.to5MinutesAfter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ScheduleAppointmentViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    private val appointmentRepository: AppointmentRepository,
    private val preferenceRepository: PreferenceRepository,
    private val genericRepository: GenericRepository,
    private val patientLastUpdatedRepository: PatientLastUpdatedRepository
) : BaseViewModel() {
    var isLaunched by mutableStateOf(false)
    var showDatePicker by mutableStateOf(false)
    var selectedDate by mutableStateOf(Date().tomorrow())
    var weekList by mutableStateOf(selectedDate.toWeekList())
    var selectedSlot by mutableStateOf("")
    var patient by mutableStateOf<PatientResponse?>(null)
    var ifRescheduling by mutableStateOf(false)
    var appointment by mutableStateOf<AppointmentResponseLocal?>(null)

    val maxNumberOfSlots = 6

    internal fun getBookedSlotsCount(time: Long, slotsCount: (Int) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            slotsCount(
                scheduleRepository.getBookedSlotsCount(time)
            )
        }
    }

    internal fun insertScheduleAndAppointment(appointmentCreated: (Any) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            // check if appointment already exists for that date
            // if exists, reschedule that appointment
            // else create new appointment
            appointmentRepository.getAppointmentsOfPatientByDate(
                patient!!.id,
                selectedDate.toTodayStartDate(),
                selectedDate.toEndOfDay()
            ).let { existingAppointment ->
                if (existingAppointment != null) {
                    // appointment already exists for that day
                    appointmentCreated(false)
                } else {
                    var id = UUIDBuilder.generateUUID()
                    var scheduleFhirId: String? = null
                    var scheduleId = selectedSlot.toCurrentTimeInMillis(
                        selectedDate
                    )
                    scheduleRepository.getScheduleByStartTime(
                        scheduleId
                    ).let { scheduleResponse ->
                        if (scheduleResponse != null) {
                            id = scheduleResponse.uuid
                            scheduleFhirId = scheduleResponse.scheduleId
                            scheduleId = scheduleResponse.planningHorizon.start.time
                            updateSchedule(scheduleResponse)
                        } else {
                            createNewSchedule(id)
                        }
                    }.also {
                        val appointmentId = UUIDBuilder.generateUUID()
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
                        appointmentCreated(
                            appointmentRepository.addAppointment(
                                AppointmentResponseLocal(
                                    appointmentId = null,
                                    uuid = appointmentId,
                                    patientId = patient?.id!!,
                                    scheduleId = Date(scheduleId),
                                    createdOn = createdOn,
                                    orgId = preferenceRepository.getOrganizationFhirId(),
                                    slot = slot,
                                    status = AppointmentStatusEnum.SCHEDULED.value,
                                    appointmentType = AppointmentTypeEnum.ROUTINE.code,
                                    inProgressTime = null
                                )
                            ).also {
                                genericRepository.insertAppointment(
                                    AppointmentResponse(
                                        appointmentId = null,
                                        uuid = appointmentId,
                                        patientFhirId = patient!!.fhirId ?: patient!!.id,
                                        scheduleId = scheduleFhirId ?: id,
                                        createdOn = createdOn,
                                        orgId = preferenceRepository.getOrganizationFhirId(),
                                        slot = slot,
                                        status = AppointmentStatusEnum.SCHEDULED.value,
                                        appointmentType = AppointmentTypeEnum.ROUTINE.code,
                                        inProgressTime = null
                                    )
                                )
                                val patientLastUpdatedResponse = PatientLastUpdatedResponse(
                                    uuid = patient!!.id,
                                    timestamp = Date()
                                )
                                patientLastUpdatedRepository.insertPatientLastUpdatedData(
                                    patientLastUpdatedResponse
                                )
                                genericRepository.insertPatientLastUpdated(
                                    patientLastUpdatedResponse
                                )
                            }
                        )
                    }
                }
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
                            status = appointment!!.status,
                            appointmentType = appointment!!.appointmentType,
                            inProgressTime = appointment!!.inProgressTime
                        )
                    ).also {
                        if (appointment?.appointmentId.isNullOrBlank()) {
                            // if fhir id is null, insert post request
                            genericRepository.insertAppointment(
                                AppointmentResponse(
                                    scheduleId = scheduleFhirId ?: id,
                                    createdOn = createdOn,
                                    slot = slot,
                                    patientFhirId = patient!!.fhirId ?: patient!!.id,
                                    appointmentId = null,
                                    orgId = appointment!!.orgId,
                                    status = appointment!!.status,
                                    uuid = appointment!!.uuid,
                                    appointmentType = appointment!!.appointmentType,
                                    inProgressTime = appointment!!.inProgressTime
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
                        val patientLastUpdatedResponse = PatientLastUpdatedResponse(
                            uuid = patient!!.id,
                            timestamp = Date()
                        )
                        patientLastUpdatedRepository.insertPatientLastUpdatedData(
                            patientLastUpdatedResponse
                        )
                        genericRepository.insertPatientLastUpdated(patientLastUpdatedResponse)
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