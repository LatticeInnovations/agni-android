package com.latticeonfhir.android.ui.appointments.schedule

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.logicalId
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.enums.AppointmentStatusFhir
import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepository
import com.latticeonfhir.android.utils.builders.UUIDBuilder
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.to30MinutesAfter
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.to5MinutesAfter
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toCurrentTimeInMillis
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toWeekList
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.tomorrow
import com.latticeonfhir.android.utils.fhirengine.FhirQueries.createNewAppointment
import com.latticeonfhir.android.utils.fhirengine.FhirQueries.createScheduleResource
import com.latticeonfhir.android.utils.fhirengine.FhirQueries.createSlotResource
import com.latticeonfhir.android.utils.fhirengine.FhirQueries.getNumberOfAppointmentsByScheduleId
import com.latticeonfhir.android.utils.fhirengine.FhirQueries.getScheduleByTime
import com.latticeonfhir.android.utils.fhirengine.FhirQueries.getTodayScheduledAppointmentOfPatient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Appointment
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.ResourceType
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ScheduleAppointmentViewModel @Inject constructor(
    private val fhirEngine: FhirEngine,
    private val preferenceRepository: PreferenceRepository
) : BaseViewModel() {
    var isLaunched by mutableStateOf(false)
    var showDatePicker by mutableStateOf(false)
    var isScheduling by mutableStateOf(false)
    var selectedDate by mutableStateOf(Date().tomorrow())
    var weekList by mutableStateOf(selectedDate.toWeekList())
    var selectedSlot by mutableStateOf("")
    var patient by mutableStateOf(Patient())
    var ifRescheduling by mutableStateOf(false)
    var appointment by mutableStateOf<Appointment?>(null)

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
                    getNumberOfAppointmentsByScheduleId(
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
                isScheduling = true
                // create appointment
                createNewAppointment(
                    fhirEngine = fhirEngine,
                    patientId = patient.logicalId,
                    locationId = preferenceRepository.getLocationFhirId(),
                    scheduleStartTime = Date(
                        selectedSlot.toCurrentTimeInMillis(
                            selectedDate
                        )
                    ),
                    scheduleEndTime = Date(
                        selectedSlot.to30MinutesAfter(
                            selectedDate
                        )
                    ),
                    slotStartTime = Date(
                        selectedSlot.toCurrentTimeInMillis(
                            selectedDate
                        )
                    ),
                    slotEndTime = Date(
                        selectedSlot.to5MinutesAfter(
                            selectedDate
                        )
                    ),
                    appointmentStatus = Appointment.AppointmentStatus.PROPOSED,
                    typeOfAppointment = AppointmentStatusFhir.SCHEDULE.type
                )
                appointmentCreated(true)
            }
        }
    }

    internal fun ifAnotherAppointmentExists(appointmentExists: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val existingAppointment = getTodayScheduledAppointmentOfPatient(
                fhirEngine,
                patient.logicalId,
                Date(selectedDate.toTodayStartDate()),
                Date(selectedDate.toEndOfDay())
            )
            if (existingAppointment == null || existingAppointment.logicalId == appointment!!.logicalId) appointmentExists(
                false
            )
            else {
                appointmentExists(true)
            }
        }
    }

    internal fun rescheduleAppointment(rescheduled: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            // free the slot of previous schedule
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
            fhirEngine.create(
                createSlotResource(
                    slotId = slotId,
                    scheduleId = scheduleId,
                    startTime = startTime,
                    endTime = slotEndTime
                )
            )
            fhirEngine.update(
                appointment!!.apply {
                    slot.clear()
                    slot.add(
                        Reference("${ResourceType.Slot.name}/$slotId")
                    )
                    start = startTime
                }
            )
            rescheduled()
        }
    }
}