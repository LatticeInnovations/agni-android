package com.heartcare.agni.ui.appointments

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.local.enums.AppointmentStatusEnum
import com.heartcare.agni.data.local.enums.ChangeTypeEnum
import com.heartcare.agni.data.local.model.appointment.AppointmentResponseLocal
import com.heartcare.agni.data.local.model.patch.ChangeRequest
import com.heartcare.agni.data.local.repository.appointment.AppointmentRepository
import com.heartcare.agni.data.local.repository.generic.GenericRepository
import com.heartcare.agni.data.local.repository.patient.lastupdated.PatientLastUpdatedRepository
import com.heartcare.agni.data.local.repository.schedule.ScheduleRepository
import com.heartcare.agni.data.server.model.patient.PatientLastUpdatedResponse
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.data.server.model.scheduleandappointment.appointment.AppointmentResponse
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AppointmentsScreenViewModel @Inject constructor(
    private val appointmentRepository: AppointmentRepository,
    private val genericRepository: GenericRepository,
    private val scheduleRepository: ScheduleRepository,
    private val patientLastUpdatedRepository: PatientLastUpdatedRepository
) : BaseViewModel() {
    var isLaunched by mutableStateOf(false)

    var patient by mutableStateOf<PatientResponse?>(null)

    val tabs = listOf("Upcoming", "Past")

    var isFabSelected by mutableStateOf(false)
    var showAllSlotsBookedDialog by mutableStateOf(false)
    var rescheduled by mutableStateOf(false)
    var scheduled by mutableStateOf(false)

    var showCancelAppointmentDialog by mutableStateOf(false)
    var selectedAppointment by mutableStateOf<AppointmentResponseLocal?>(null)
    var upcomingAppointmentsList by mutableStateOf(
        listOf<AppointmentResponseLocal>()
    )

    var pastAppointmentsList by mutableStateOf(listOf<AppointmentResponseLocal>())

    internal fun getAppointmentsList(patientId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            upcomingAppointmentsList = appointmentRepository.getAppointmentsOfPatientByStatus(
                patientId,
                AppointmentStatusEnum.SCHEDULED.value
            ).filter { appointmentResponseLocal ->
                appointmentResponseLocal.slot.start.time > Date().toTodayStartDate()
            }
            pastAppointmentsList = appointmentRepository.getAppointmentsOfPatient(patientId)
                .filter { appointmentResponseLocal ->
                    appointmentResponseLocal.slot.start.time < Date().toEndOfDay() && appointmentResponseLocal.status != AppointmentStatusEnum.SCHEDULED.value
                }
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
                    // update previous schedule
                    scheduleRepository.getScheduleByStartTime(selectedAppointment?.scheduleId?.time!!)
                        .let { scheduleResponse ->
                            scheduleResponse?.let { previousScheduleResponse ->
                                scheduleRepository.updateSchedule(
                                    previousScheduleResponse.copy(
                                        bookedSlots = scheduleResponse.bookedSlots?.minus(1)
                                    )
                                )
                            }
                        }
                    if (selectedAppointment?.appointmentId.isNullOrBlank()) {
                        // if fhir id is null, insert post request
                        genericRepository.insertAppointment(
                            AppointmentResponse(
                                appointmentId = null,
                                uuid = selectedAppointment!!.uuid,
                                patientFhirId = patient!!.fhirId ?: patient!!.id,
                                scheduleId = (scheduleRepository.getScheduleByStartTime(
                                    selectedAppointment!!.scheduleId.time
                                )?.scheduleId ?: scheduleRepository.getScheduleByStartTime(
                                    selectedAppointment!!.scheduleId.time
                                )?.uuid)!!,
                                slot = selectedAppointment!!.slot,
                                orgId = selectedAppointment!!.orgId,
                                createdOn = selectedAppointment!!.createdOn,
                                status = AppointmentStatusEnum.CANCELLED.value,
                                appointmentType = selectedAppointment!!.appointmentType,
                                inProgressTime = selectedAppointment!!.inProgressTime
                            )
                        )
                    } else {
                        // insert patch request
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