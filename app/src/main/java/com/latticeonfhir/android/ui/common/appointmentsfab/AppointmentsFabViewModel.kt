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
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.to30MinutesAfter
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.to5MinutesAfter
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toAppointmentTime
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toCurrentTimeInMillis
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toSlotStartTime
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import com.latticeonfhir.android.utils.fhirengine.FhirQueries
import com.latticeonfhir.android.utils.fhirengine.FhirQueries.getAppointmentToday
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
            ).forEach { searchResult ->
                if (!searchResult.included.isNullOrEmpty()) ifAlreadyWaiting = true
            }
            ifAllSlotsBooked = getTotalNumberOfAppointmentsToday(fhirEngine) >= 80
            appointment = getTodayScheduledAppointmentOfPatient(
                fhirEngine,
                patientId,
                startTime = Date(Date().toTodayStartDate()),
                endTime = Date(Date().toEndOfDay())
            )
        }
    }

    internal fun addPatientToQueue(patient: Patient, addedToQueue: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            FhirQueries.createNewAppointment(
                fhirEngine = fhirEngine,
                patientId = patient.logicalId,
                locationId = preferenceRepository.getLocationFhirId(),
                scheduleStartTime = Date(
                    Date().toSlotStartTime().toCurrentTimeInMillis(
                        Date()
                    )
                ),
                scheduleEndTime = Date(
                    Date().toSlotStartTime().to30MinutesAfter(
                        Date()
                    )
                ),
                slotStartTime = Date(Date().toAppointmentTime().toCurrentTimeInMillis(Date())),
                slotEndTime = Date(
                    Date().toAppointmentTime().to5MinutesAfter(
                        Date()
                    )
                ),
                appointmentStatus = Appointment.AppointmentStatus.ARRIVED,
                typeOfAppointment = AppointmentStatusFhir.WALK_IN.type
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