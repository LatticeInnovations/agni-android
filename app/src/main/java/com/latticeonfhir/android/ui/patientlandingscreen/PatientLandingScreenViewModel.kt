package com.latticeonfhir.android.ui.patientlandingscreen

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import com.latticeonfhir.android.FhirApp
import com.latticeonfhir.android.base.viewmodel.BaseAndroidViewModel
import com.latticeonfhir.android.data.local.enums.AppointmentStatusEnum
import com.latticeonfhir.android.data.local.repository.appointment.AppointmentRepository
import com.latticeonfhir.android.data.local.repository.cvd.records.CVDAssessmentRepository
import com.latticeonfhir.android.data.local.repository.patient.PatientRepository
import com.latticeonfhir.android.data.local.repository.prescription.PrescriptionRepository
import com.latticeonfhir.android.data.local.repository.vaccination.ImmunizationRecommendationRepository
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.service.workmanager.utils.Sync
import com.latticeonfhir.android.service.workmanager.workers.trigger.TriggerWorkerPeriodicImpl
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toEndOfDay
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toTodayStartDate
import com.latticeonfhir.android.utils.network.CheckNetwork
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
    application: Application,
    private val patientRepository: PatientRepository,
    private val appointmentRepository: AppointmentRepository,
    private val prescriptionRepository: PrescriptionRepository,
    private val cvdAssessmentRepository: CVDAssessmentRepository,
    private val immunizationRecommendationRepository: ImmunizationRecommendationRepository
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

    private val syncService by lazy { getApplication<FhirApp>().syncService }

    private suspend fun syncData() {
        Sync.getWorkerInfo<TriggerWorkerPeriodicImpl>(getApplication<FhirApp>().applicationContext)
            .collectLatest { workInfo ->
                if (workInfo != null && workInfo.state == WorkInfo.State.ENQUEUED) {
                    getApplication<FhirApp>().launchSyncing()
                }
            }
    }

    internal fun downloadPrescriptions(patientFhirId: String) {
        if (CheckNetwork.isInternetAvailable(getApplication<FhirApp>().applicationContext)) {
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