package com.latticeonfhir.android.ui.symptomsanddiagnosis

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.enums.AppointmentStatusEnum
import com.latticeonfhir.android.data.local.model.appointment.AppointmentResponseLocal
import com.latticeonfhir.android.data.local.repository.appointment.AppointmentRepository
import com.latticeonfhir.android.data.local.repository.symptomsanddiagnosis.SymDiagRepository
import com.latticeonfhir.android.data.local.roomdb.dao.SymptomsAndDiagnosisDao
import com.latticeonfhir.android.data.local.roomdb.entities.symptomsanddiagnosis.SymptomsAndDiagnosisLocal
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.data.server.repository.symptomsanddiagnosis.SymptomsAndDiagnosisRepository
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.convertedDate
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
    private val appointmentRepository: AppointmentRepository
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


    internal fun getSymptomsAndDiagnosis(ioDispatcher: CoroutineDispatcher = Dispatchers.IO) {
        viewModelScope.launch(ioDispatcher) {
            if (dao.getSymptomsEntity().isEmpty()) {
                getAndInsertSymptoms()
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

}