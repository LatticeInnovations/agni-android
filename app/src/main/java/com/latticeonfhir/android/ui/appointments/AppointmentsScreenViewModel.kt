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
        }
    }

    // TODO: after queue screen
//    internal fun cancelAppointment(cancelled: (Int) -> Unit) {
//        viewModelScope.launch(Dispatchers.IO) {
//            cancelled(
//                appointmentRepository.updateAppointment(
//                    selectedAppointment!!.copy(
//                        status = AppointmentStatusEnum.CANCELLED.value
//                    )
//                ).also {
//                    // update previous schedule
//                    scheduleRepository.getScheduleByStartTime(selectedAppointment?.scheduleId?.time!!)
//                        .let { scheduleResponse ->
//                            scheduleResponse?.let { previousScheduleResponse ->
//                                scheduleRepository.updateSchedule(
//                                    previousScheduleResponse.copy(
//                                        bookedSlots = scheduleResponse.bookedSlots?.minus(1)
//                                    )
//                                )
//                            }
//                        }
//                    if (selectedAppointment?.appointmentId.isNullOrBlank()) {
                        // if fhir id is null, insert post request
//                        genericRepository.insertAppointment(
//                            AppointmentResponse(
//                                appointmentId = null,
//                                uuid = selectedAppointment!!.uuid,
//                                patientFhirId = patient?.fhirId ?: patient?.id,
//                                scheduleId = (scheduleRepository.getScheduleByStartTime(
//                                    selectedAppointment!!.scheduleId.time
//                                )?.scheduleId ?: scheduleRepository.getScheduleByStartTime(
//                                    selectedAppointment!!.scheduleId.time
//                                )?.uuid)!!,
//                                slot = selectedAppointment!!.slot,
//                                orgId = selectedAppointment!!.orgId,
//                                createdOn = selectedAppointment!!.createdOn,
//                                status = AppointmentStatusEnum.CANCELLED.value
//                            )
//                        )
//                    } else {
//                        // insert patch request
//                        genericRepository.insertOrUpdateAppointmentPatch(
//                            appointmentFhirId = selectedAppointment?.appointmentId!!,
//                            map = mapOf(
//                                Pair(
//                                    "status",
//                                    ChangeRequest(
//                                        value = AppointmentStatusEnum.CANCELLED.value,
//                                        operation = ChangeTypeEnum.REPLACE.value
//                                    )
//                                )
//                            )
//                        )
//                    }
//                }
//            )
//        }
//    }
}