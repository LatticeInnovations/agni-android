package com.latticeonfhir.android.ui.vitalsscreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.data.local.enums.AppointmentStatusEnum
import com.latticeonfhir.android.data.local.model.appointment.AppointmentResponseLocal
import com.latticeonfhir.android.data.local.model.vital.VitalLocal
import com.latticeonfhir.android.data.local.repository.appointment.AppointmentRepository
import com.latticeonfhir.android.data.local.repository.vital.VitalRepository
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
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
class VitalsViewModel @Inject constructor(
    private val vitalRepository: VitalRepository,
    private val appointmentRepository: AppointmentRepository
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
}