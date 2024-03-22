package com.latticeonfhir.android.ui.landingscreen

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.sync.CurrentSyncJobStatus
import com.latticeonfhir.android.FhirApp
import com.latticeonfhir.android.R
import com.latticeonfhir.android.base.viewmodel.BaseAndroidViewModel
import com.latticeonfhir.android.data.local.enums.AppointmentStatusEnum
import com.latticeonfhir.android.data.local.model.appointment.QueueData
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.to14DaysWeek
import com.latticeonfhir.android.utils.fhirengine.FhirQueries.getAllAppointmentByDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Appointment
import org.hl7.fhir.r4.model.Encounter
import org.hl7.fhir.r4.model.Patient
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class QueueViewModel @Inject constructor(
    application: Application,
    private val fhirEngine: FhirEngine
) : BaseAndroidViewModel(application) {

    // queue screen
    var isLaunched by mutableStateOf(false)
    var isLoading by mutableStateOf(true)
    var selectedDate by mutableStateOf(Date())
    var weekList by mutableStateOf(selectedDate.to14DaysWeek())
    var showDatePicker by mutableStateOf(false)
    var appointmentsList by mutableStateOf(listOf<QueueData>())
    var showCancelAppointmentDialog by mutableStateOf(false)
    var statusList by mutableStateOf(listOf<String>())
    var isSearchingInQueue by mutableStateOf(false)
    var searchQueueQuery by mutableStateOf("")
    var waitingQueueList by mutableStateOf(listOf<QueueData>())
    var inProgressQueueList by mutableStateOf(listOf<QueueData>())
    var scheduledQueueList by mutableStateOf(listOf<QueueData>())
    var completedQueueList by mutableStateOf(listOf<QueueData>())
    var cancelledQueueList by mutableStateOf(listOf<QueueData>())
    var noShowQueueList by mutableStateOf(listOf<QueueData>())
    var queueDataSelected by mutableStateOf<QueueData?>(null)
    var selectedChip by mutableIntStateOf(R.string.total_appointment)
    var rescheduled by mutableStateOf(false)

    internal fun syncData(ioDispatcher: CoroutineDispatcher = Dispatchers.IO) {
        viewModelScope.launch(ioDispatcher) {
            if (getApplication<FhirApp>().periodicSyncJobStatus.value?.currentSyncJobStatus !is CurrentSyncJobStatus.Running) {
                FhirApp.runEnqueuedWorker(getApplication<FhirApp>().applicationContext)
                getAppointmentListByDate()
            }
        }
    }

    internal fun getAppointmentListByDate(ioDispatcher: CoroutineDispatcher = Dispatchers.IO) {
        viewModelScope.launch(ioDispatcher) {
            appointmentsList = getAllAppointmentByDate(
                fhirEngine,
                selectedDate
            ).map { result ->
                QueueData(
                    encounter = result.resource,
                    patient = result.included?.get(Encounter.SUBJECT.paramName)?.get(0) as Patient,
                    appointment = result.included?.get(Encounter.APPOINTMENT.paramName)?.get(0) as Appointment
                )
            }.filter {
                it.patient.nameFirstRep.nameAsSingleString.contains(searchQueueQuery, ignoreCase = true)
            }.sortedBy {
                it.appointment.start
            }
            waitingQueueList = appointmentsList.filter { queueData ->
                (queueData.encounter.status == Encounter.EncounterStatus.PLANNED
                        && queueData.appointment.status == Appointment.AppointmentStatus.ARRIVED)
            }
            inProgressQueueList = appointmentsList.filter { queueData ->
                (queueData.encounter.status == Encounter.EncounterStatus.INPROGRESS
                        && queueData.appointment.status == Appointment.AppointmentStatus.ARRIVED)
            }
            scheduledQueueList = appointmentsList.filter { queueData ->
                (queueData.encounter.status == Encounter.EncounterStatus.PLANNED
                        && queueData.appointment.status == Appointment.AppointmentStatus.PROPOSED)
            }
            completedQueueList = appointmentsList.filter { queueData ->
                (queueData.encounter.status == Encounter.EncounterStatus.FINISHED
                        && queueData.appointment.status == Appointment.AppointmentStatus.ARRIVED)
            }
            cancelledQueueList = appointmentsList.filter { queueData ->
                (queueData.encounter.status == Encounter.EncounterStatus.PLANNED
                        || queueData.encounter.status == Encounter.EncounterStatus.INPROGRESS
                        || queueData.encounter.status == Encounter.EncounterStatus.FINISHED)
                        && queueData.appointment.status == Appointment.AppointmentStatus.CANCELLED
            }
            noShowQueueList = appointmentsList.filter { queueData ->
                (queueData.encounter.status == Encounter.EncounterStatus.PLANNED
                        || queueData.encounter.status == Encounter.EncounterStatus.INPROGRESS
                        || queueData.encounter.status == Encounter.EncounterStatus.FINISHED)
                        && queueData.appointment.status == Appointment.AppointmentStatus.NOSHOW
            }
            isLoading = false
        }
    }

    internal fun cancelAppointment(cancelled: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            fhirEngine.update(
                queueDataSelected!!.appointment.apply {
                    status = Appointment.AppointmentStatus.CANCELLED
                }
            )
            cancelled()
        }
    }

    internal fun updateAppointmentStatus(updatedStatus: String, updated: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            when(updatedStatus) {
                AppointmentStatusEnum.ARRIVED.value -> {
                    fhirEngine.update(
                        queueDataSelected!!.appointment.apply {
                            status = Appointment.AppointmentStatus.ARRIVED
                        }
                    )
                }
                AppointmentStatusEnum.COMPLETED.value -> {
                    fhirEngine.update(
                        queueDataSelected!!.encounter.apply {
                            status = Encounter.EncounterStatus.FINISHED
                        }
                    )
                }
            }
            updated()
        }
    }
}
