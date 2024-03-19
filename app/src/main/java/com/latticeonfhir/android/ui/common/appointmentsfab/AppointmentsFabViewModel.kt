package com.latticeonfhir.android.ui.common.appointmentsfab

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
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toAppointmentTime
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toCurrentTimeInMillis
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toSlotStartTime
import com.latticeonfhir.android.utils.fhirengine.FhirQueries.createAppointmentResource
import com.latticeonfhir.android.utils.fhirengine.FhirQueries.createEncounterResource
import com.latticeonfhir.android.utils.fhirengine.FhirQueries.createScheduleResource
import com.latticeonfhir.android.utils.fhirengine.FhirQueries.createSlotResource
import com.latticeonfhir.android.utils.fhirengine.FhirQueries.getAppointmentToday
import com.latticeonfhir.android.utils.fhirengine.FhirQueries.getScheduleByTime
import com.latticeonfhir.android.utils.fhirengine.FhirQueries.getTodayScheduledAppointmentOfPatient
import com.latticeonfhir.android.utils.fhirengine.FhirQueries.getTotalNumberOfAppointmentsToday
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Appointment
import org.hl7.fhir.r4.model.Patient
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AppointmentsFabViewModel @Inject constructor(
    private val fhirEngine: FhirEngine,
    private val preferenceRepository: PreferenceRepository
) : BaseViewModel() {

    var appointment by mutableStateOf<Appointment?>(null)
    var ifAlreadyWaiting by mutableStateOf(false)
    var ifAllSlotsBooked by mutableStateOf(false)
    var adding by mutableStateOf(false)

    internal fun initialize(patientId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getAppointmentToday(
                fhirEngine, patientId
            ).forEach { _ ->
                ifAlreadyWaiting = true
            }
            ifAllSlotsBooked = getTotalNumberOfAppointmentsToday(fhirEngine) >= 80
            appointment = getTodayScheduledAppointmentOfPatient(fhirEngine, patientId)
        }
    }

    internal fun addPatientToQueue(patient: Patient, addedToQueue: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            var scheduleId = UUIDBuilder.generateUUID()
            val scheduleStartTime = Date(
                Date().toSlotStartTime().toCurrentTimeInMillis(
                    Date()
                )
            )
            val scheduleEndTime = Date(
                Date().toSlotStartTime().to30MinutesAfter(
                    Date()
                )
            )
            val slotStartTime = Date(Date().toAppointmentTime().toCurrentTimeInMillis(Date()))
            val slotEndTime = Date(
                Date().toAppointmentTime().to5MinutesAfter(
                    Date()
                )
            )
            val scheduleResource = getScheduleByTime(
                fhirEngine,
                scheduleStartTime,
                scheduleEndTime
            )
            if (scheduleResource != null){
                scheduleId = scheduleResource.logicalId
            } else {
                // create a schedule
                createScheduleResource(
                    fhirEngine,
                    scheduleId,
                    preferenceRepository.getLocationFhirId(),
                    scheduleStartTime,
                    scheduleEndTime
                )
            }
            val slotId = UUIDBuilder.generateUUID()
            val appointmentId = UUIDBuilder.generateUUID()
            fhirEngine.create(
                createSlotResource(
                    slotId = slotId,
                    scheduleId = scheduleId,
                    startTime = slotStartTime,
                    endTime = slotEndTime
                ),
                createAppointmentResource(
                    patientId = patient.logicalId,
                    locationId = preferenceRepository.getLocationFhirId(),
                    appointmentId = appointmentId,
                    appointmentStatus = Appointment.AppointmentStatus.ARRIVED,
                    typeOfAppointment = AppointmentStatusFhir.WALK_IN.type,
                    startTime = slotStartTime,
                    slotId = slotId
                ),
                createEncounterResource(
                    patientId = patient.logicalId,
                    encounterId = UUIDBuilder.generateUUID(),
                    appointmentId = appointmentId
                )
            )
            addedToQueue()
        }
    }

    internal fun updateStatusToArrived(
        appointment: Appointment,
        updated: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            fhirEngine.update(
                appointment.apply {
                    status = Appointment.AppointmentStatus.ARRIVED
                }
            )
            updated()
        }
    }
}