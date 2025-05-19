package com.latticeonfhir.features.patient.ui.patientlandingscreen

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import com.latticeonfhir.core.base.viewmodel.BaseAndroidViewModel
import com.latticeonfhir.core.data.repository.local.appointment.AppointmentRepository
import com.latticeonfhir.core.data.repository.local.cvd.records.CVDAssessmentRepository
import com.latticeonfhir.core.data.repository.local.patient.PatientRepository
import com.latticeonfhir.core.data.repository.local.prescription.PrescriptionRepository
import com.latticeonfhir.core.data.repository.local.vaccination.ImmunizationRecommendationRepository
import com.latticeonfhir.core.model.enums.AppointmentStatusEnum
import com.latticeonfhir.core.model.server.patient.PatientResponse
import com.latticeonfhir.sync.workmanager.workmanager.utils.Sync
import com.latticeonfhir.sync.workmanager.workmanager.workers.trigger.TriggerWorkerPeriodicImpl
import com.latticeonfhir.core.utils.converters.TimeConverter.toEndOfDay
import com.latticeonfhir.core.utils.converters.TimeConverter.toTodayStartDate
import com.latticeonfhir.core.utils.network.CheckNetwork
import com.latticeonfhir.sync.workmanager.sync.SyncService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.internal.filterList
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class PatientLandingScreenViewModel @Inject constructor(
    private val application: Application,
    private val patientRepository: PatientRepository,
    private val appointmentRepository: AppointmentRepository,
    private val prescriptionRepository: PrescriptionRepository,
    private val cvdAssessmentRepository: CVDAssessmentRepository,
    private val immunizationRecommendationRepository: ImmunizationRecommendationRepository,
    private val syncService: SyncService
) : BaseAndroidViewModel(application) {
    var isLaunched by mutableStateOf(false)
    var patient by mutableStateOf<PatientResponse?>(null)

    private var logoutUser by mutableStateOf(false)
    private var logoutReason by mutableStateOf("")

    var appointmentsCount by mutableIntStateOf(0)
    var uploadsCount by mutableIntStateOf(0)
    var pastAppointmentsCount by mutableIntStateOf(0)
    var isFabSelected by mutableStateOf(false)
    var showAllSlotsBookedDialog by mutableStateOf(false)

    var cvdRisk by mutableStateOf("")

    var selectedIndex by mutableIntStateOf(0)

    var upcomingVaccine by mutableIntStateOf(0)
    var missedVaccine by mutableIntStateOf(0)
    var takenVaccine by mutableIntStateOf(0)

    private suspend fun syncData() {
        Sync.getWorkerInfo<TriggerWorkerPeriodicImpl>(application)
            .collectLatest { workInfo ->
                if (workInfo != null && workInfo.state == WorkInfo.State.ENQUEUED) {
                    syncService.syncLauncher { logout, error ->

                    }
                }
            }
    }

    internal fun downloadPrescriptions(patientFhirId: String) {
        if (CheckNetwork.isInternetAvailable(application)) {
            viewModelScope.launch(Dispatchers.IO) {
                syncService.patchPrescription { isErrorReceived, errorMsg ->
                    if (isErrorReceived) {
                        logoutUser = true
                        logoutReason = errorMsg
                    }
                }
                syncService.downloadFormPrescription(patientFhirId) { isErrorReceived, errorMsg ->
                    if (isErrorReceived) {
                        logoutUser = true
                        logoutReason = errorMsg
                    }
                }
                syncService.downloadPhotoPrescription(patientFhirId) { isErrorReceived, errorMsg ->
                    if (isErrorReceived) {
                        logoutUser = true
                        logoutReason = errorMsg
                    }
                }
                getUploadsCount(patient!!.id)
                getImmunizationRecommendationList(patient!!.id)
                syncData()
            }
        }
    }

    internal suspend fun getPatientData(id: String): PatientResponse {
        return patientRepository.getPatientById(id)[0]
    }

    internal fun getScheduledAppointmentsCount(patientId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            appointmentsCount = appointmentRepository.getAppointmentsOfPatientByStatus(
                patientId,
                AppointmentStatusEnum.SCHEDULED.value
            ).filter { appointmentResponseLocal ->
                appointmentResponseLocal.slot.start.time > Date().toTodayStartDate()
            }.size
            pastAppointmentsCount = appointmentRepository.getAppointmentsOfPatient(patientId)
                .filter { appointmentResponseLocal ->
                    appointmentResponseLocal.slot.start.time < Date().toEndOfDay() && appointmentResponseLocal.status != AppointmentStatusEnum.SCHEDULED.value
                }.size
        }
    }

    internal fun getUploadsCount(patientId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            uploadsCount = prescriptionRepository.getLastPhotoPrescription(patientId).size
        }
    }

    internal fun getLastCVDRisk(patientId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            cvdRisk = (cvdAssessmentRepository.getCVDRecord(patientId).firstOrNull()?.risk ?: "").toString()
        }
    }

    internal fun getImmunizationRecommendationList(
        patientId: String,
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    ) {
        viewModelScope.launch(ioDispatcher) {
            val immunizationRecommendationList = immunizationRecommendationRepository.getImmunizationRecommendation(patientId)
            missedVaccine = immunizationRecommendationList.filterList { vaccineStartDate < Date(Date().toTodayStartDate()) && takenOn == null }.sortedBy { it.vaccineStartDate }.size
            takenVaccine = immunizationRecommendationList.filterList { takenOn != null }.sortedByDescending { it.takenOn }.size
            upcomingVaccine = immunizationRecommendationList.size - missedVaccine - takenVaccine
        }
    }
}