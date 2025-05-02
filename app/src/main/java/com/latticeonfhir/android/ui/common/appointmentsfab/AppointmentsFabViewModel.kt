package com.latticeonfhir.core.ui.common.appointmentsfab

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.core.data.local.enums.AppointmentStatusEnum
import com.latticeonfhir.android.data.local.model.appointment.AppointmentResponseLocal
import com.latticeonfhir.core.data.local.repository.appointment.AppointmentRepository
import com.latticeonfhir.core.data.local.repository.generic.GenericRepository
import com.latticeonfhir.core.data.local.repository.patient.lastupdated.PatientLastUpdatedRepository
import com.latticeonfhir.core.data.local.repository.preference.PreferenceRepository
import com.latticeonfhir.android.data.local.repository.schedule.ScheduleRepository
import com.latticeonfhir.core.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.utils.common.Queries
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AppointmentsFabViewModel @Inject constructor(
    private val appointmentRepository: AppointmentRepository,
    private val scheduleRepository: ScheduleRepository,
    private val genericRepository: GenericRepository,
    private val preferenceRepository: PreferenceRepository,
    private val patientLastUpdatedRepository: PatientLastUpdatedRepository
) : BaseViewModel() {

    var appointment by mutableStateOf<AppointmentResponseLocal?>(null)
    var ifAlreadyWaiting by mutableStateOf(false)
    var ifAllSlotsBooked by mutableStateOf(false)
    private val maxNumberOfAppointmentsInADay = 250

    internal fun initialize(patientId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            appointmentRepository.getAppointmentsOfPatientByDate(
                patientId,
                Date().toTodayStartDate(),
                Date().toEndOfDay()
            ).let { appointmentResponse ->
                ifAlreadyWaiting = appointmentResponse?.status?.let {
                    it != AppointmentStatusEnum.SCHEDULED.value
                } ?: false
            }
            ifAllSlotsBooked = appointmentRepository.getAppointmentListByDate(
                Date().toTodayStartDate(),
                Date().toEndOfDay()
            ).filter { appointmentResponseLocal ->
                appointmentResponseLocal.status != AppointmentStatusEnum.CANCELLED.value
            }.size >= maxNumberOfAppointmentsInADay
            appointment = appointmentRepository.getAppointmentsOfPatientByStatus(
                patientId,
                AppointmentStatusEnum.SCHEDULED.value
            ).firstOrNull { appointmentResponse ->
                appointmentResponse.slot.start.time < Date().toEndOfDay() && appointmentResponse.slot.start.time > Date().toTodayStartDate()
            }
        }
    }

    internal fun addPatientToQueue(patient: PatientResponse, addedToQueue: (List<Long>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
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
        updated: (Int) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
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