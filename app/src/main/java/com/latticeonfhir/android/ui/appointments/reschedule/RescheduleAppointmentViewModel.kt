package com.latticeonfhir.android.ui.appointments.reschedule

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.enums.AppointmentStatusEnum
import com.latticeonfhir.android.data.local.enums.ChangeTypeEnum
import com.latticeonfhir.android.data.local.model.appointment.AppointmentResponseLocal
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
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toCurrentTimeInMillis
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toWeekList
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.tomorrow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class RescheduleAppointmentViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    private val appointmentRepository: AppointmentRepository,
    private val preferenceRepository: PreferenceRepository,
    private val genericRepository: GenericRepository
) : BaseViewModel() {
    var isLaunched by mutableStateOf(false)
    var appointment by mutableStateOf<AppointmentResponseLocal?>(null)
    var patient by mutableStateOf<PatientResponse?>(null)
    var showDatePicker by mutableStateOf(false)
    var selectedDate by mutableStateOf(Date().tomorrow())
    var weekList by mutableStateOf(selectedDate.toWeekList())
    var selectedSlot by mutableStateOf("")

    internal fun getBookedSlotsCount(time: Long, slotsCount: (Int) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            slotsCount(
                scheduleRepository.getBookedSlotsCount(time)
            )
        }
    }

    internal fun rescheduleAppointment(rescheduled: (Int) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            // free the slot of previous schedule
            scheduleRepository.getScheduleByStartTime(appointment!!.scheduleId.time).let { scheduleResponse ->
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
            scheduleRepository.getScheduleByStartTime(scheduleId
            ).let { scheduleResponse ->
                // if already exists, increase booked slots count
                if (scheduleResponse != null) {
                    scheduleId = scheduleResponse.planningHorizon.start.time
                    id = scheduleRepository.getScheduleByStartTime(scheduleId)?.uuid!!
                    scheduleFhirId = scheduleResponse.scheduleId
                    scheduleRepository.updateSchedule(
                        scheduleResponse.copy(
                            bookedSlots = scheduleResponse.bookedSlots!! + 1
                        )
                    )
                } else {
                    // create new schedule
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
                                    patientFhirId = patient?.fhirId ?: patient?.id,
                                    appointmentId = appointment!!.appointmentId,
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
}