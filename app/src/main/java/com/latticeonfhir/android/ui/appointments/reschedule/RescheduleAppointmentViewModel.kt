package com.latticeonfhir.android.ui.appointments.reschedule

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
    var appointment by mutableStateOf<AppointmentResponse?>(null)
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
            scheduleRepository.getScheduleById(appointment!!.scheduleId).let { scheduleResponse ->
                scheduleRepository.updateSchedule(
                    scheduleResponse.copy(
                        bookedSlots = scheduleResponse.bookedSlots?.minus(1)
                    )
                )
            }
            // check for new schedule
            var scheduleId = UUIDBuilder.generateUUID()
            var scheduleFhirId: String? = null
            scheduleRepository.getScheduleByStartTime(
                selectedSlot.toCurrentTimeInMillis(
                    selectedDate
                )
            ).let { scheduleResponse ->
                // if already exists, increase booked slots count
                if (scheduleResponse != null) {
                    scheduleId = scheduleResponse.uuid
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
                            uuid = scheduleId,
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
                            uuid = scheduleId,
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
                        appointment!!.copy(
                            scheduleId = scheduleId,
                            createdOn = createdOn,
                            slot = slot
                        )
                    ).also {
                        // send patch request in generic
                        genericRepository.insertOrUpdateAppointmentPatch(
                            appointmentFhirId = appointment?.appointmentId ?:appointment!!.uuid,
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
                                        value = scheduleFhirId ?: scheduleId
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
                )
            }
        }
    }
}