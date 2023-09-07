package com.latticeonfhir.android.ui.patientlandingscreen

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import com.latticeonfhir.android.FhirApp
import com.latticeonfhir.android.base.viewmodel.BaseAndroidViewModel
import com.latticeonfhir.android.data.local.enums.AppointmentStatusEnum
import com.latticeonfhir.android.data.local.repository.appointment.AppointmentRepository
import com.latticeonfhir.android.data.local.repository.patient.PatientRepository
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.service.workmanager.utils.Sync
import com.latticeonfhir.android.service.workmanager.workers.trigger.TriggerWorkerPeriodicImpl
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import com.latticeonfhir.android.utils.network.CheckNetwork
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class PatientLandingScreenViewModel @Inject constructor(
    application: Application,
    private val patientRepository: PatientRepository,
    private val appointmentRepository: AppointmentRepository
) : BaseAndroidViewModel(application) {
    var isLaunched by mutableStateOf(false)
    var patient by mutableStateOf<PatientResponse?>(null)

    private var logoutUser by mutableStateOf(false)
    private var logoutReason by mutableStateOf("")

    var appointmentsCount by mutableStateOf(0)
    var isFabSelected by mutableStateOf(false)
    var showAllSlotsBookedDialog by mutableStateOf(false)

    private val syncService by lazy { getApplication<FhirApp>().syncService }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            Sync.getWorkerInfo<TriggerWorkerPeriodicImpl>(getApplication<FhirApp>().applicationContext).collectLatest { workInfo ->
                if (workInfo != null && workInfo.state == WorkInfo.State.ENQUEUED) {
                    syncService.syncLauncher { isErrorReceived, errorMsg ->
                        if (isErrorReceived) {
                            logoutUser = true
                            logoutReason = errorMsg
                        }
                    }
                }
            }
        }
    }

    internal fun downloadPrescriptions(patientFhirId: String) {
        if (CheckNetwork.isInternetAvailable(getApplication<FhirApp>().applicationContext)) {
            viewModelScope.launch(Dispatchers.IO) {
                syncService.downloadPrescription(patientFhirId) { isErrorReceived, errorMsg ->
                    if (isErrorReceived) {
                        logoutUser = true
                        logoutReason = errorMsg
                    }
                }
            }
        }
    }

    internal suspend fun getPatientData(id: String): PatientResponse {
        return patientRepository.getPatientById(id)[0]
    }

    internal fun getScheduledAppointmentsCount(patientId: String) {
        viewModelScope.launch {
            appointmentsCount = appointmentRepository.getAppointmentsOfPatientByStatus(
                patientId,
                AppointmentStatusEnum.SCHEDULED.value
            ).filter { appointmentResponseLocal ->
                appointmentResponseLocal.slot.start.time > Date().toTodayStartDate()
            }.size
        }
    }
}