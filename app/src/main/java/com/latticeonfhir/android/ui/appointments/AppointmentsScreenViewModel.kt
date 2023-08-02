package com.latticeonfhir.android.ui.appointments

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
import com.latticeonfhir.android.data.local.repository.schedule.ScheduleRepository
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.data.server.model.scheduleandappointment.appointment.AppointmentResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppointmentsScreenViewModel @Inject constructor(
    private val appointmentRepository: AppointmentRepository,
    private val genericRepository: GenericRepository,
    private val scheduleRepository: ScheduleRepository
) : BaseViewModel() {
    var isLaunched by mutableStateOf(false)

    var patient by mutableStateOf<PatientResponse?>(null)

    val tabs = listOf("Upcoming", "Completed")

    var isFabSelected by mutableStateOf(false)

    var showCancelAppointmentDialog by mutableStateOf(false)
    var selectedAppointment by mutableStateOf<AppointmentResponse?>(null)

    var upcomingAppointmentsList by mutableStateOf(
        listOf<AppointmentResponse>()
    )

    var completedAppointmentsList by mutableStateOf(listOf<AppointmentResponse>())

    internal fun getAppointmentsList(patientId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            upcomingAppointmentsList = appointmentRepository.getAppointmentsOfPatientByStatus(
                patientId,
                AppointmentStatusEnum.SCHEDULED.value
            )
            completedAppointmentsList = appointmentRepository.getAppointmentsOfPatientByStatus(
                patientId,
                AppointmentStatusEnum.COMPLETED.value
            )
        }
    }

    internal fun cancelAppointment(cancelled: (Int) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            cancelled(
                appointmentRepository.updateAppointment(
                    selectedAppointment!!.copy(
                        status = AppointmentStatusEnum.CANCELLED.value
                    )
                ).also {
                    scheduleRepository.getScheduleById(selectedAppointment?.scheduleId!!).let {  scheduleResponse ->
                        scheduleRepository.updateSchedule(
                            scheduleResponse.copy(
                                bookedSlots = scheduleResponse.bookedSlots?.minus(1)
                            )
                        )
                    }
                    genericRepository.insertOrUpdateAppointmentPatch(
                        appointmentFhirId = selectedAppointment?.appointmentId!!,
                        map = mapOf(
                            Pair(
                                "status",
                                ChangeRequest(
                                    value = AppointmentStatusEnum.CANCELLED.value,
                                    operation = ChangeTypeEnum.REPLACE.value
                                )
                            )
                        )
                    )
                }
            )
        }
    }
}