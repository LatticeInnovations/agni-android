package com.latticeonfhir.features.vitals

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.core.data.local.model.vital.VitalLocal
import com.latticeonfhir.core.data.repository.local.appointment.AppointmentRepository
import com.latticeonfhir.core.data.repository.local.cvd.records.CVDAssessmentRepository
import com.latticeonfhir.core.data.repository.local.generic.GenericRepository
import com.latticeonfhir.core.data.repository.local.patient.lastupdated.PatientLastUpdatedRepository
import com.latticeonfhir.core.data.repository.local.preference.PreferenceRepository
import com.latticeonfhir.core.data.repository.local.schedule.ScheduleRepository
import com.latticeonfhir.core.data.repository.local.vital.VitalRepository
import com.latticeonfhir.core.data.utils.common.Queries
import com.latticeonfhir.core.model.enums.AppointmentStatusEnum
import com.latticeonfhir.core.model.local.appointment.AppointmentResponseLocal
import com.latticeonfhir.core.model.server.cvd.CVDResponse
import com.latticeonfhir.core.model.server.patient.PatientResponse
import com.latticeonfhir.core.utils.constants.VitalConstants.ALL
import com.latticeonfhir.core.utils.converters.responseconverter.TimeConverter.convertedDate
import com.latticeonfhir.core.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.latticeonfhir.core.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class VitalsViewModel @Inject constructor(

    private val vitalRepository: VitalRepository,
    private val appointmentRepository: AppointmentRepository,
    private val preferenceRepository: PreferenceRepository,
    private val genericRepository: GenericRepository,
    private val scheduleRepository: ScheduleRepository,
    private val patientLastUpdatedRepository: PatientLastUpdatedRepository,
    private val cvdAssessmentRepository: CVDAssessmentRepository

) : ViewModel() {
    var isVitalExist by mutableStateOf(false)
    private var vitals = MutableStateFlow<List<VitalLocal>>(emptyList())
    var _vitals: StateFlow<List<VitalLocal>> = vitals
    var vital: VitalLocal? = null
    var isLaunched by mutableStateOf(false)
    var patient by mutableStateOf<PatientResponse?>(null)
    var isAppointmentExist by mutableStateOf(false)
    internal var appointmentResponseLocal: AppointmentResponseLocal? = null
    var isWeightSelected by mutableStateOf(true)
    var isHRSelected by mutableStateOf(false)
    var isRRSelected by mutableStateOf(false)
    var isSpO2Selected by mutableStateOf(false)
    var isGlucoseSelected by mutableStateOf(false)
    var isBPSelected by mutableStateOf(false)
    var msg by mutableStateOf("")
    var isFistLaunch by mutableStateOf(false)

    var appointment by mutableStateOf<AppointmentResponseLocal?>(null)
    var canAddAssessment by mutableStateOf(false)
    var showAddToQueueDialog by mutableStateOf(false)
    var ifAllSlotsBooked by mutableStateOf(false)
    var showAllSlotsBookedDialog by mutableStateOf(false)
    private val maxNumberOfAppointmentsInADay = 250
    var showAppointmentCompletedDialog by mutableStateOf(false)
    var isAppointmentCompleted by mutableStateOf(false)
    var selectedOption by mutableStateOf(ALL)

    var previousRecords by mutableStateOf(listOf<CVDResponse>())

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
                canAddAssessment =
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

    internal fun getStudentTodayAppointment(
        startDate: Date, endDate: Date, patientId: String,
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO,

        ) {
        viewModelScope.launch(ioDispatcher)
        {
            appointmentResponseLocal =
                appointmentRepository.getAppointmentListByDate(startDate.time, endDate.time)
                    .firstOrNull { appointmentEntity ->
                        appointmentEntity.patientId == patientId && appointmentEntity.status != AppointmentStatusEnum.CANCELLED.value
                    }
        }
    }

    internal fun getVitals(
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) {
        viewModelScope.launch(ioDispatcher) {
            val list = vitalRepository.getLastVital(patient!!.id)
            vitals.value =
                list.sortedByDescending { it.createdOn }
            if (vitals.value.isNotEmpty()) {
                isVitalExist = true
                val vitalList =
                    vitals.value.filter { it.createdOn.convertedDate() == Date().convertedDate() }
                if (vitalList.isNotEmpty()) {
                    vital = vitalList[0]
                }
                appointmentResponseLocal?.let {
                    isAppointmentExist = true
                }
            }

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
        ioDispatcher: CoroutineDispatcher=Dispatchers.IO,
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

    internal fun getRecords(ioDispatcher: CoroutineDispatcher=Dispatchers.IO) {
        viewModelScope.launch(ioDispatcher) {
            previousRecords = cvdAssessmentRepository.getCVDRecord(patient!!.id)
        }
    }
}

data class CombineVitalAndCVDRecord(val type: String, val date: Date, val content: Any)