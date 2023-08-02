package com.latticeonfhir.android.ui.appointments.schedule

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.enums.AppointmentStatusEnum
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
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ScheduleAppointmentViewModel @Inject constructor(
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
    var patient by mutableStateOf<PatientResponse?>(null)

    internal fun getBookedSlotsCount(time: Long, slotsCount: (Int) -> Unit) {
        viewModelScope.launch {
            slotsCount(
                scheduleRepository.getBookedSlotsCount(time)
            )
        }
    }

    internal fun insertScheduleAndAppointment(appointmentCreated: (List<Long>) -> Unit) {
        viewModelScope.launch {
            var scheduleId = UUIDBuilder.generateUUID()
            var scheduleFhirId: String? = null
            scheduleRepository.getScheduleByStartTime(
                selectedSlot.toCurrentTimeInMillis(
                    selectedDate
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
                val appointmentId = UUIDBuilder.generateUUID()
                val createdOn = Date()
                appointmentCreated(
                    appointmentRepository.addAppointment(
                        AppointmentResponse(
                            appointmentId = null,
                            uuid = appointmentId,
                            patientFhirId = patient?.id,
                            scheduleId = scheduleId,
                            createdOn = createdOn,
                            orgId = preferenceRepository.getOrganizationFhirId(),
                            slot = Slot(
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
                            ),
                            status = AppointmentStatusEnum.SCHEDULED.value
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
                                ),
                                status = AppointmentStatusEnum.SCHEDULED.value
                            )
                        )
                    }
                )
            }
        }
    }
}