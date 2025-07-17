package com.heartcare.agni.ui.historyandtests

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.local.enums.AppointmentStatusEnum
import com.heartcare.agni.data.local.model.appointment.AppointmentResponseLocal
import com.heartcare.agni.data.local.repository.appointment.AppointmentRepository
import com.heartcare.agni.data.local.repository.generic.GenericRepository
import com.heartcare.agni.data.local.repository.patient.lastupdated.PatientLastUpdatedRepository
import com.heartcare.agni.data.local.repository.preference.PreferenceRepository
import com.heartcare.agni.data.local.repository.schedule.ScheduleRepository
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.utils.common.Queries
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class HistoryTakingAndTestsViewModel@Inject constructor(
    private val appointmentRepository: AppointmentRepository,
    private val preferenceRepository: PreferenceRepository,
    private val genericRepository: GenericRepository,
    private val scheduleRepository: ScheduleRepository,
    private val patientLastUpdatedRepository: PatientLastUpdatedRepository
): BaseViewModel() {
    var isLaunched by mutableStateOf(false)

    var patient by mutableStateOf<PatientResponse?>(null)

    var priorDxList by mutableStateOf(listOf("", "", ""))

    var appointment by mutableStateOf<AppointmentResponseLocal?>(null)
    var canAddAssessment by mutableStateOf(false)
    var showAddToQueueDialog by mutableStateOf(false)
    var ifAllSlotsBooked by mutableStateOf(false)
    var showAllSlotsBookedDialog by mutableStateOf(false)
    private val maxNumberOfAppointmentsInADay = 250
    var showAppointmentCompletedDialog by mutableStateOf(false)
    var isAppointmentCompleted by mutableStateOf(false)

    internal fun getAppointmentInfo(
        callback: () -> Unit,
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    ) {
        viewModelScope.launch(ioDispatcher) {
            val todayStart = Date().toTodayStartDate()
            val todayEnd = Date().toEndOfDay()
            val patientId = patient?.id ?: return@launch callback()

            val appointmentsToday = appointmentRepository.getAppointmentsOfPatientByDate(
                patientId, todayStart, todayEnd
            )

            // Determine if assessment can be added
            appointmentsToday?.let {
                val status = it.status
                canAddAssessment = status == AppointmentStatusEnum.ARRIVED.value ||
                        status == AppointmentStatusEnum.WALK_IN.value ||
                        status == AppointmentStatusEnum.IN_PROGRESS.value

                isAppointmentCompleted = status == AppointmentStatusEnum.COMPLETED.value
            }

            // Get the appointment matching today's time window and scheduled status
            appointment = appointmentRepository
                .getAppointmentsOfPatientByStatus(patientId, AppointmentStatusEnum.SCHEDULED.value)
                .firstOrNull { it.slot.start.time in todayStart..todayEnd }

            // Check if all slots are booked
            val bookedAppointments = appointmentRepository.getAppointmentListByDate(todayStart, todayEnd)
                .count { it.status != AppointmentStatusEnum.CANCELLED.value }

            ifAllSlotsBooked = bookedAppointments >= maxNumberOfAppointmentsInADay

            callback()
        }
    }


    internal fun addPatientToQueue(
        patient: PatientResponse,
        addedToQueue: (List<Long>) -> Unit,
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    ) {
        viewModelScope.launch(ioDispatcher) {
            Queries.addPatientToQueue(
                patient,
                scheduleRepository,
                genericRepository,
                preferenceRepository,
                appointmentRepository,
                patientLastUpdatedRepository,
                addedToQueue
            )
        }
    }

    internal fun updateStatusToArrived(
        patient: PatientResponse,
        appointment: AppointmentResponseLocal,
        updated: (Int) -> Unit,
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    ) {
        viewModelScope.launch(ioDispatcher) {
            Queries.updateStatusToArrived(
                patient,
                appointment,
                appointmentRepository,
                genericRepository,
                preferenceRepository,
                scheduleRepository,
                patientLastUpdatedRepository,
                updated
            )
        }
    }
}