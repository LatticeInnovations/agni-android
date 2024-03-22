package com.latticeonfhir.android.ui.appointments

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.logicalId
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.utils.fhirengine.FhirQueries.getCompletedAppointments
import com.latticeonfhir.android.utils.fhirengine.FhirQueries.getScheduledAppointments
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Appointment
import org.hl7.fhir.r4.model.Encounter
import org.hl7.fhir.r4.model.Patient
import javax.inject.Inject

@HiltViewModel
class AppointmentsScreenViewModel @Inject constructor(
    private val fhirEngine: FhirEngine
) : BaseViewModel() {
    var isLaunched by mutableStateOf(false)
    var isCancelling by mutableStateOf(false)

    var patient by mutableStateOf(Patient())
    private var appointmentsIds = mutableSetOf<String>()

    val tabs = listOf("Upcoming", "Completed")

    var isFabSelected by mutableStateOf(false)
    var showAllSlotsBookedDialog by mutableStateOf(false)
    var rescheduled by mutableStateOf(false)
    var scheduled by mutableStateOf(false)

    var showCancelAppointmentDialog by mutableStateOf(false)
    var selectedAppointment by mutableStateOf<Appointment?>(null)
    var upcomingAppointmentsList by mutableStateOf(
        listOf<Appointment>()
    )

    var completedAppointmentsList by mutableStateOf(listOf<Appointment>())

    internal fun getAppointmentsList() {
        viewModelScope.launch(Dispatchers.IO) {
            appointmentsIds.clear()
            upcomingAppointmentsList = listOf()
            completedAppointmentsList = listOf()
            getScheduledAppointments(fhirEngine, patient.logicalId).forEach { result ->
                result.included?.get(Encounter.APPOINTMENT.paramName)?.forEach { appointment ->
                    upcomingAppointmentsList.forEach {
                        appointmentsIds.add(it.logicalId)
                    }
                    if (!appointmentsIds.contains(appointment.logicalId)) upcomingAppointmentsList = upcomingAppointmentsList + listOf(appointment as Appointment)
                }
            }
            getCompletedAppointments(fhirEngine, patient.logicalId).forEach { result ->
                result.included?.get(Encounter.APPOINTMENT.paramName)?.forEach { appointment ->
                    completedAppointmentsList.forEach {
                        appointmentsIds.add(it.logicalId)
                    }
                    if (!appointmentsIds.contains(appointment.logicalId)) completedAppointmentsList = completedAppointmentsList + listOf(appointment as Appointment)
                }
            }
            upcomingAppointmentsList = upcomingAppointmentsList.sortedBy {
                it.start
            }
            completedAppointmentsList = completedAppointmentsList.sortedByDescending {
                it.start
            }
        }
    }

    internal fun cancelAppointment(cancelled: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            fhirEngine.update(
                selectedAppointment!!.apply {
                    status = Appointment.AppointmentStatus.CANCELLED
                }
            )
            cancelled()
        }
    }
}