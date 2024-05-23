package com.latticeonfhir.android.ui.landingscreen

import android.app.Application
import android.app.job.JobScheduler
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.await
import com.latticeonfhir.android.FhirApp
import com.latticeonfhir.android.R
import com.latticeonfhir.android.base.viewmodel.BaseAndroidViewModel
import com.latticeonfhir.android.data.local.enums.SyncStatusMessageEnum
import com.latticeonfhir.android.data.local.enums.WorkerStatus
import com.latticeonfhir.android.data.local.model.search.SearchParameters
import com.latticeonfhir.android.data.local.repository.appointment.AppointmentRepository
import com.latticeonfhir.android.data.local.repository.patient.PatientRepository
import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepository
import com.latticeonfhir.android.data.local.repository.search.SearchRepository
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.service.sync.SyncService
import com.latticeonfhir.android.service.workmanager.request.WorkRequestBuilders
import com.latticeonfhir.android.service.workmanager.utils.Delay
import com.latticeonfhir.android.service.workmanager.utils.Sync
import com.latticeonfhir.android.service.workmanager.workers.trigger.TriggerWorkerPeriodicImpl
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.calculateMinutesToOneThirty
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toLastSyncTime
import com.latticeonfhir.android.utils.network.CheckNetwork
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class LandingScreenViewModel @Inject constructor(
    application: Application,
    private val patientRepository: PatientRepository,
    private val searchRepository: SearchRepository,
    private val preferenceRepository: PreferenceRepository,
    private val appointmentRepository: AppointmentRepository
) : BaseAndroidViewModel(application) {

    private val workRequestBuilders: WorkRequestBuilders by lazy { (application as FhirApp).workRequestBuilder }
    private val syncService: SyncService by lazy { (application as FhirApp).syncService }

    var isLaunched by mutableStateOf(false)
    var isLoading by mutableStateOf(true)
    val items = listOf("My Patients", "Queue", "Profile")
    var isSearching by mutableStateOf(false)
    var isSearchingByQuery by mutableStateOf(false)
    var isSearchResult by mutableStateOf(false)
    var searchQuery by mutableStateOf("")
    var selectedIndex by mutableIntStateOf(0)
    var patientList: Flow<PagingData<PatientResponse>> by mutableStateOf(flowOf())
    var searchResultList: Flow<PagingData<PatientResponse>> by mutableStateOf(flowOf())
    var searchParameters by mutableStateOf<SearchParameters?>(null)
    var previousSearchList = mutableListOf<String>()
    var size by mutableIntStateOf(0)
    var isLoggingOut by mutableStateOf(false)
    var addedToQueue by mutableStateOf(false)
    var patientArrived by mutableStateOf(false)

    // user details
    var userName by mutableStateOf("")
    var userRole by mutableStateOf("")
    var userPhoneNo by mutableStateOf("")
    var userEmail by mutableStateOf("")

    var logoutUser by mutableStateOf(false)
    var logoutReason by mutableStateOf("")

    // queue screen
    var showStatusChangeLayout by mutableStateOf(false)

    // syncing
    var syncStatus by mutableStateOf(WorkerStatus.TODO)
    var syncIcon by mutableIntStateOf(R.drawable.sync_icon)
    var syncStatusMessage by mutableStateOf(SyncStatusMessageEnum.SYNCING_IN_PROGRESS.message)
    var lastSyncDate by mutableStateOf("")
    var syncStatusDisplay by mutableStateOf("")
    var syncIconDisplay by mutableIntStateOf(0)

    init {
        viewModelScope.launch {
            getApplication<FhirApp>().syncWorkerStatus.observeForever { workerStatus ->
                when (workerStatus) {
                    WorkerStatus.IN_PROGRESS -> {
                        syncStatus = WorkerStatus.IN_PROGRESS
                        syncIcon = R.drawable.sync_icon
                        syncStatusMessage = SyncStatusMessageEnum.SYNCING_IN_PROGRESS.message
                        setSyncDisplayData()
                    }
                    WorkerStatus.SUCCESS -> {
                        syncStatus = WorkerStatus.SUCCESS
                        syncIcon = R.drawable.sync_completed_icon
                        syncStatusMessage = SyncStatusMessageEnum.SYNCING_COMPLETED.message
                        setSyncDisplayData()
                        CoroutineScope(Dispatchers.IO).launch {
                            delay(20000)
                            hideSyncStatus()
                        }
                    }
                    WorkerStatus.FAILED -> {
                        syncIcon = R.drawable.sync_problem
                        syncStatus = WorkerStatus.FAILED
                        syncStatusMessage = SyncStatusMessageEnum.SYNCING_FAILED.message
                        setSyncDisplayData()
                        CoroutineScope(Dispatchers.IO).launch {
                            delay(20000)
                            hideSyncStatus()
                        }
                    }
                    else -> Timber.d("Worker Status $workerStatus")
                }
            }
        }

        viewModelScope.launch {
            getApplication<FhirApp>().sessionExpireFlow.asFlow().collectLatest { sessionExpireMap ->
                if (sessionExpireMap["errorReceived"] == true) {
                    logoutUser = true
                    logoutReason = sessionExpireMap["errorMsg"]?.toString() ?: "SERVER ERROR"
                }
            }
        }

        //Medication Sync
        if (CheckNetwork.isInternetAvailable(getApplication<Application>().applicationContext)) {
            viewModelScope.launch(Dispatchers.IO) {
                syncService.downloadMedication { isErrorReceived, errorMsg ->
                    if (isErrorReceived) {
                        logoutUser = true
                        logoutReason = errorMsg
                    }
                }
            }
        }

        // Trigger Periodic Sync Worker
        viewModelScope.launch(Dispatchers.IO) {
            workRequestBuilders.setPeriodicTriggerWorker()
        }

        // Trigger Periodic Update Appointment No Show Status Worker
        viewModelScope.launch(Dispatchers.IO) {
            workRequestBuilders.setPeriodicAppointmentNoShowStatusUpdateWorker(
                null,
                Delay(
                    Date().calculateMinutesToOneThirty(),
                    TimeUnit.MINUTES
                )
            )
        }

        // Trigger Periodic Update Appointment Completed Status Worker
        viewModelScope.launch(Dispatchers.IO) {
            workRequestBuilders.setPeriodicAppointmentCompletedStatusUpdateWorker(
                null,
                Delay(
                    Date().calculateMinutesToOneThirty(),
                    TimeUnit.MINUTES
                )
            )
        }

        userName = preferenceRepository.getUserName()
        userRole = preferenceRepository.getUserRole()
        userPhoneNo = preferenceRepository.getUserMobile().toString()
        userEmail = preferenceRepository.getUserEmail()

        setSyncDisplayData()
    }

    internal fun syncData() {
        viewModelScope.launch(Dispatchers.IO) {
            Sync.getWorkerInfo<TriggerWorkerPeriodicImpl>(getApplication<FhirApp>().applicationContext)
                .collectLatest { workInfo ->
                    if (workInfo != null && workInfo.state == WorkInfo.State.ENQUEUED) {
                        getApplication<FhirApp>().launchSyncing()
                    }
                }
        }
    }

    internal fun hideSyncStatus() {
        if (syncStatus != WorkerStatus.IN_PROGRESS) syncStatus = WorkerStatus.TODO
    }

    private fun setSyncDisplayData() {
        lastSyncDate = if (preferenceRepository.getLastSyncTime() != 0L)
            Date(preferenceRepository.getLastSyncTime()).toLastSyncTime()
        else "Unavailable"
        syncStatusDisplay = preferenceRepository.getSyncStatus()
        syncIconDisplay = when(syncStatusDisplay) {
            SyncStatusMessageEnum.SYNCING_IN_PROGRESS.display -> R.drawable.sync_icon
            SyncStatusMessageEnum.SYNCING_COMPLETED.display -> {
                R.drawable.sync_completed_icon
            }
            SyncStatusMessageEnum.SYNCING_FAILED.display -> R.drawable.sync_problem
            else -> 0
        }
    }

    private fun getPatientList() {
        viewModelScope.launch(Dispatchers.IO) {
            patientList = patientRepository.getPatientList().asFlow().cachedIn(viewModelScope)
        }
    }

    fun populateList() {
        size = 0
        searchResultList = flowOf()
        isLoading = true
        if (isSearchResult) {
            if (isSearchingByQuery) searchPatientByQuery()
            else searchPatient(searchParameters!!)
        } else {
            getPatientList()
        }
    }

    internal fun getPreviousSearches() {
        viewModelScope.launch(Dispatchers.IO) {
            previousSearchList = searchRepository.getRecentPatientSearches().toMutableList()
        }
    }

    internal fun insertRecentSearch() {
        viewModelScope.launch(Dispatchers.IO) {
            searchRepository.insertRecentPatientSearch(searchQuery.trim())
        }
    }

    private fun searchPatient(searchParameters: SearchParameters) {
        viewModelScope.launch(Dispatchers.IO) {
            searchResultList =
                searchRepository.searchPatients(searchParameters, searchRepository.getSearchList())
                    .map { data ->
                        data.map { paginationResponse ->
                            size = paginationResponse.size
                            paginationResponse.data
                        }
                    }.cachedIn(viewModelScope)
            isLoading = false
        }
    }

    private fun searchPatientByQuery() {
        viewModelScope.launch(Dispatchers.IO) {
            searchResultList = searchRepository.searchPatientByQuery(
                searchQuery.trim(),
                searchRepository.getSearchList()
            ).map { data ->
                data.map { paginationResponse ->
                    size = paginationResponse.size
                    paginationResponse.data
                }
            }.cachedIn(viewModelScope)
            isLoading = false
        }
    }

    internal fun getLastVisitedOfPatient(
        patientId: String,
        lastVisited: (Date?) -> Unit
    ) {
        viewModelScope.launch {
            val appointment = appointmentRepository.getLastCompletedAppointment(patientId)
            lastVisited(appointment?.startTime)
        }
    }

    internal fun logout() {
        viewModelScope.launch(Dispatchers.Default) {
            WorkManager.getInstance(getApplication<Application>().applicationContext)
                .cancelAllWork().await().also {
                    (getApplication<FhirApp>().applicationContext.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler).cancelAll()
                    preferenceRepository.resetAuthenticationToken()
                }
        }
    }
}