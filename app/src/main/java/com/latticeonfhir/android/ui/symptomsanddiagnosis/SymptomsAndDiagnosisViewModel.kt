package com.latticeonfhir.android.ui.symptomsanddiagnosis

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.enums.AppointmentStatusEnum
import com.latticeonfhir.android.data.local.model.appointment.AppointmentResponseLocal
import com.latticeonfhir.android.data.local.repository.appointment.AppointmentRepository
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.patient.lastupdated.PatientLastUpdatedRepository
import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepository
import com.latticeonfhir.android.data.local.repository.schedule.ScheduleRepository
import com.latticeonfhir.android.data.local.repository.symptomsanddiagnosis.SymDiagRepository
import com.latticeonfhir.android.data.local.roomdb.dao.SymptomsAndDiagnosisDao
import com.latticeonfhir.android.data.local.roomdb.entities.symptomsanddiagnosis.SymptomsAndDiagnosisLocal
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.data.server.repository.symptomsanddiagnosis.SymptomsAndDiagnosisRepository
import com.latticeonfhir.android.utils.common.Queries
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.convertedDate
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class SymptomsAndDiagnosisViewModel @Inject constructor(
    private val repository: SymptomsAndDiagnosisRepository,
    private val symDiagRepository: SymDiagRepository,
    private val dao: SymptomsAndDiagnosisDao,
    private val appointmentRepository: AppointmentRepository,
    private val preferenceRepository: PreferenceRepository,
    private val genericRepository: GenericRepository,
    private val scheduleRepository: ScheduleRepository,
    private val patientLastUpdatedRepository: PatientLastUpdatedRepository,
) : BaseViewModel() {
    var isLaunched by mutableStateOf(false)
    var isSymptomsAndDiagnosisExist by mutableStateOf(false)
    private var symptomsDiagnosisList =
        MutableStateFlow<List<SymptomsAndDiagnosisLocal>>(emptyList())
    var symptomsDiagnosisListFlow: StateFlow<List<SymptomsAndDiagnosisLocal>> =
        symptomsDiagnosisList

    var patient by mutableStateOf<PatientResponse?>(null)
    var isAppointmentExist by mutableStateOf(false)
    internal var appointmentResponseLocal: AppointmentResponseLocal? = null
    var symptomsAndDiagnosisLocal by mutableStateOf<SymptomsAndDiagnosisLocal?>(null)
    var msg by mutableStateOf("")

    var appointment by mutableStateOf<AppointmentResponseLocal?>(null)
    var canAddAssessment by mutableStateOf(false)
    var showAddToQueueDialog by mutableStateOf(false)
    var ifAllSlotsBooked by mutableStateOf(false)
    var showAllSlotsBookedDialog by mutableStateOf(false)
    private val maxNumberOfAppointmentsInADay = 250
    var showAppointmentCompletedDialog by mutableStateOf(false)
    var isAppointmentCompleted by mutableStateOf(false)


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
                    }.also {
                        isAppointmentExist = true
                    }
        }
    }

    private fun getAndInsertSymptoms(ioDispatcher: CoroutineDispatcher = Dispatchers.IO) {
        viewModelScope.launch(ioDispatcher) {
            repository.insertSymptoms()
        }
    }


    private fun getAndInsertDiagnosis(ioDispatcher: CoroutineDispatcher = Dispatchers.IO) {
        viewModelScope.launch(ioDispatcher) {
            repository.insertDiagnosis()


        }
    }


    internal fun getSymptomsAndDiagnosis(ioDispatcher: CoroutineDispatcher = Dispatchers.IO) {
        viewModelScope.launch(ioDispatcher) {
            if (dao.getSymptomsEntity().isEmpty() || dao.getDiagnosisEntity().isEmpty()) {
                getAndInsertSymptoms()
                getAndInsertDiagnosis()
            }

        }
    }

    internal fun getSymDiagnosis(
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) {
        viewModelScope.launch(ioDispatcher) {
            val list =
                symDiagRepository.getPastSymptomsAndDiagnosis(patient!!.id)
            symptomsDiagnosisList.value = list.sortedByDescending { it.createdOn }
            if (symptomsDiagnosisList.value.isNotEmpty()) {
                isSymptomsAndDiagnosisExist = true
                val vitalList =
                    symptomsDiagnosisList.value.filter { it.createdOn.convertedDate() == Date().convertedDate() }
                if (vitalList.isNotEmpty()) {
                    symptomsAndDiagnosisLocal = vitalList[0]
                }
                appointmentResponseLocal?.let {
                    isAppointmentExist = true
                }
            }
            isAppointmentExist = true

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

}