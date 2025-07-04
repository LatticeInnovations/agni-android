package com.heartcare.agni.ui.vaccination

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heartcare.agni.data.local.enums.AppointmentStatusEnum
import com.heartcare.agni.data.local.model.appointment.AppointmentResponseLocal
import com.heartcare.agni.data.local.model.vaccination.ImmunizationRecommendation
import com.heartcare.agni.data.local.repository.appointment.AppointmentRepository
import com.heartcare.agni.data.local.repository.generic.GenericRepository
import com.heartcare.agni.data.local.repository.patient.lastupdated.PatientLastUpdatedRepository
import com.heartcare.agni.data.local.repository.preference.PreferenceRepository
import com.heartcare.agni.data.local.repository.schedule.ScheduleRepository
import com.heartcare.agni.data.local.repository.vaccination.ImmunizationRecommendationRepository
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.utils.common.Queries
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.internal.filterList
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class VaccinationViewModel @Inject constructor(
    private val immunizationRecommendationRepository: ImmunizationRecommendationRepository,
    private val appointmentRepository: AppointmentRepository,
    private val scheduleRepository: ScheduleRepository,
    private val patientLastUpdatedRepository: PatientLastUpdatedRepository,
    private val preferenceRepository: PreferenceRepository,
    private val genericRepository: GenericRepository
) : ViewModel() {
    var isLaunched by mutableStateOf(false)
    var isVaccineAdded by mutableStateOf(false)
    var patient by mutableStateOf<PatientResponse?>(null)
    var selectedVaccine by mutableStateOf<ImmunizationRecommendation?>(null)

    val tabs = listOf("All", "Missed", "Taken")

    var immunizationRecommendationList by mutableStateOf(listOf<ImmunizationRecommendation>())
    var missedImmunizationRecommendationList by mutableStateOf(listOf<ImmunizationRecommendation>())
    var takenImmunizationRecommendationList by mutableStateOf(listOf<ImmunizationRecommendation>())

    var canAddVaccination by mutableStateOf(false)
    var showAddToQueueDialog by mutableStateOf(false)
    var ifAllSlotsBooked by mutableStateOf(false)
    var isAppointmentCompleted by mutableStateOf(false)
    var showAppointmentCompletedDialog by mutableStateOf(false)
    var showAllSlotsBookedDialog by mutableStateOf(false)
    private val maxNumberOfAppointmentsInADay = 250
    var appointment by mutableStateOf<AppointmentResponseLocal?>(null)

    var index by mutableIntStateOf(0)

    internal fun getAppointmentInfo(
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
        callback: () -> Unit
    ) {
        viewModelScope.launch(ioDispatcher) {
            appointment = appointmentRepository.getAppointmentsOfPatientByStatus(
                patient!!.id,
                AppointmentStatusEnum.SCHEDULED.value
            ).firstOrNull { appointmentResponse ->
                appointmentResponse.slot.start.time < Date().toEndOfDay() && appointmentResponse.slot.start.time > Date().toTodayStartDate()
            }
            appointmentRepository.getAppointmentsOfPatientByDate(
                patient!!.id,
                Date().toTodayStartDate(),
                Date().toEndOfDay()
            ).let { appointmentResponse ->
                canAddVaccination =
                    appointmentResponse?.status == AppointmentStatusEnum.ARRIVED.value || appointmentResponse?.status == AppointmentStatusEnum.WALK_IN.value
                            || appointmentResponse?.status == AppointmentStatusEnum.IN_PROGRESS.value
                isAppointmentCompleted =
                    appointmentResponse?.status == AppointmentStatusEnum.COMPLETED.value
            }
            ifAllSlotsBooked = appointmentRepository.getAppointmentListByDate(
                Date().toTodayStartDate(),
                Date().toEndOfDay()
            ).filter { appointmentResponseLocal ->
                appointmentResponseLocal.status != AppointmentStatusEnum.CANCELLED.value
            }.size >= maxNumberOfAppointmentsInADay
            callback()
        }
    }

    internal fun addPatientToQueue(
        patient: PatientResponse,
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
        addedToQueue: (List<Long>) -> Unit
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
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
        updated: (Int) -> Unit
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


    internal fun getImmunizationRecommendationAndImmunizationList(
        patientId: String,
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    ) {
        viewModelScope.launch(ioDispatcher) {
            immunizationRecommendationList = immunizationRecommendationRepository.getImmunizationRecommendation(patientId)
            missedImmunizationRecommendationList = immunizationRecommendationList.filterList { vaccineStartDate < Date(Date().toTodayStartDate()) && takenOn == null }.sortedBy { it.vaccineStartDate }
            takenImmunizationRecommendationList = immunizationRecommendationList.filterList { takenOn != null }.sortedByDescending { it.takenOn }
        }
    }


    companion object {
        const val MISSED = "missed"
        const val TAKEN = "taken"
    }
}