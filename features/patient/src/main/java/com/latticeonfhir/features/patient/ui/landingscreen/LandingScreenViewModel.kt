package com.latticeonfhir.features.patient.ui.landingscreen

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
import com.latticeonfhir.core.FhirApp
import com.latticeonfhir.core.data.repository.server.authentication.AuthenticationRepository
import com.latticeonfhir.core.base.viewmodel.BaseAndroidViewModel
import com.latticeonfhir.core.data.local.model.search.SearchParameters
import com.latticeonfhir.core.data.repository.local.appointment.AppointmentRepository
import com.latticeonfhir.core.data.repository.local.cvd.chart.RiskPredictionChartRepository
import com.latticeonfhir.core.data.repository.local.patient.PatientRepository
import com.latticeonfhir.core.data.repository.local.preference.PreferenceRepository
import com.latticeonfhir.core.data.repository.local.search.SearchRepository
import com.latticeonfhir.core.database.FhirAppDatabase
import com.latticeonfhir.core.database.entities.cvd.RiskPredictionCharts
import com.latticeonfhir.core.model.enums.LastVisit
import com.latticeonfhir.core.model.enums.RegisterTypeEnum
import com.latticeonfhir.core.model.enums.SyncStatusMessageEnum
import com.latticeonfhir.core.model.enums.WorkerStatus
import com.latticeonfhir.core.model.server.patient.PatientResponse
import com.latticeonfhir.core.service.workmanager.utils.Delay
import com.latticeonfhir.core.service.workmanager.utils.Sync
import com.latticeonfhir.core.service.workmanager.workers.trigger.TriggerWorkerPeriodicImpl
import com.latticeonfhir.core.utils.common.Queries.getSearchListWithLastVisited
import com.latticeonfhir.core.utils.constants.ErrorConstants.TOO_MANY_ATTEMPTS_ERROR
import com.latticeonfhir.core.utils.converters.TimeConverter.calculateMinutesToOneThirty
import com.latticeonfhir.core.utils.converters.TimeConverter.toLastSyncTime
import com.latticeonfhir.core.utils.converters.responsemapper.ApiEmptyResponse
import com.latticeonfhir.core.utils.converters.responsemapper.ApiEndResponse
import com.latticeonfhir.core.utils.converters.responsemapper.ApiErrorResponse
import com.latticeonfhir.core.utils.network.CheckNetwork.isInternetAvailable
import com.latticeonfhir.core.data.repository.server.signup.SignUpRepository
import com.latticeonfhir.features.patient.R
import com.latticeonfhir.sync.workmanager.request.WorkRequestBuilders
import com.latticeonfhir.sync.workmanager.sync.SyncService
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
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class LandingScreenViewModel @Inject constructor(
    application: Application,
    private val patientRepository: PatientRepository,
    private val searchRepository: SearchRepository,
    private val preferenceRepository: PreferenceRepository,
    private val appointmentRepository: AppointmentRepository,
    private val signUpRepository: SignUpRepository,
    private val authenticationRepository: AuthenticationRepository,
    private val fhirAppDatabase: FhirAppDatabase,
    private val riskPredictionChartRepository: RiskPredictionChartRepository
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
    internal var deleteAccountError by mutableStateOf("")

    var showConfirmDeleteAccountDialog by mutableStateOf(false)

    // queue screen
    var showStatusChangeLayout by mutableStateOf(false)

    // syncing
    var syncStatus by mutableStateOf(WorkerStatus.TODO)
    var syncIcon by mutableIntStateOf(R.drawable.sync_icon)
    var syncStatusMessage by mutableStateOf(SyncStatusMessageEnum.SYNCING_IN_PROGRESS.message)
    var lastSyncDate by mutableStateOf("")
    var syncStatusDisplay by mutableStateOf("")
    var syncIconDisplay by mutableIntStateOf(0)

    var twoMinuteTimer by mutableIntStateOf(120)
    var showOtpFields by mutableStateOf(false)
    var otpEntered by mutableStateOf("")
    var isOtpIncorrect by mutableStateOf(false)
    var errorMsg by mutableStateOf("")
    var otpAttemptsExpired by mutableStateOf(false)
    var isVerifying by mutableStateOf(false)
    var isResending by mutableStateOf(false)

    init {

        viewModelScope.launch(Dispatchers.IO) {
            val data = mutableListOf<RiskPredictionCharts>()
            val inputStream = application.assets.open("RiskPredictionChartSouthAsia.csv") // Assuming the file is in the assets folder
            val reader = BufferedReader(InputStreamReader(inputStream))

            reader.useLines { lines ->
                lines.drop(1).forEach { line -> // Skip header if present
                    val tokens = line.split(",")
                    if (tokens.size >= 2) {
                        data.add(
                            RiskPredictionCharts(
                                id = tokens[0].toInt(),
                                regionCode = tokens[1],
                                riskLevel = tokens[2],
                                diabetes = tokens[3],
                                gender = tokens[4],
                                smoker = tokens[5],
                                age = tokens[6].toInt(),
                                systolic = tokens[7],
                                cholesterol = tokens[8],
                                cholesterolUnit = tokens[9],
                                bmi = tokens[10],
                                bmiUnit = tokens[11],
                                hs = tokens[12],
                                hsValue = tokens[13],
                                riskLevelId = tokens[14].toInt()
                            )
                        )
                    }
                }
            }
            riskPredictionChartRepository.insertRecords(*data.toTypedArray())
        }

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
                    getApplication<FhirApp>().sessionExpireFlow.postValue(emptyMap())
                }
            }
        }

        //Medication Sync
        if (isInternetAvailable(getApplication<Application>().applicationContext)) {
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
        syncIconDisplay = when (syncStatusDisplay) {
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
            isLoading = false
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
            var finalSearchList = searchRepository.getSearchList()
            if (!searchParameters.lastFacilityVisit.isNullOrBlank() && searchParameters.lastFacilityVisit != LastVisit.NOT_APPLICABLE.label) {
                finalSearchList = getSearchListWithLastVisited(
                    searchParameters.lastFacilityVisit!!,
                    finalSearchList,
                    appointmentRepository
                )
            }
            searchResultList =
                searchRepository.searchPatients(searchParameters, finalSearchList)
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

    internal fun sendDeleteAccountOtp(navigate: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            signUpRepository.verification(
                userEmail.ifBlank { userPhoneNo },
                RegisterTypeEnum.DELETE
            ).apply {
                if (this is ApiEmptyResponse) {
                    navigate(true)
                } else if (this is ApiErrorResponse) {
                    deleteAccountError = errorMessage
                    navigate(false)
                }
            }
        }
    }

    internal fun validateOtp(navigate: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            signUpRepository.otpVerification(
                userEmail.ifBlank { userPhoneNo },
                otpEntered.toInt(),
                RegisterTypeEnum.DELETE
            ).apply {
                if (this is ApiEndResponse) {
                    deleteAccount(body.token, navigate)
                } else if (this is ApiErrorResponse) {
                    when (errorMessage) {
                        TOO_MANY_ATTEMPTS_ERROR -> {
                            isOtpIncorrect = false
                            otpAttemptsExpired = true
                        }

                        else -> isOtpIncorrect = true
                    }
                    isVerifying = false
                    errorMsg = errorMessage
                    navigate(false)
                }
            }
        }
    }

    private suspend fun deleteAccount(tempAuthToken: String, navigate: (Boolean) -> Unit) {
        authenticationRepository.deleteAccount(tempAuthToken).apply {
            if (this is ApiErrorResponse) {
                errorMsg = errorMessage
                navigate(false)
            } else if (this is ApiEndResponse) {
                logoutReason = body ?: "INTERNAL_SERVER_ERROR"
                stopWorkers()
                clearAllAppData()
                navigate(true)
            }
        }
    }

    internal fun resendOTP(resent: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            signUpRepository.verification(
                userEmail.ifBlank { userPhoneNo },
                RegisterTypeEnum.DELETE
            ).apply {
                if (this is ApiEmptyResponse) {
                    isResending = false
                    resent(true)
                } else if (this is ApiErrorResponse) {
                    when (errorMessage) {
                        TOO_MANY_ATTEMPTS_ERROR -> {
                            isOtpIncorrect = false
                            otpEntered = ""
                            otpAttemptsExpired = true
                        }

                        else -> isOtpIncorrect = true
                    }
                    errorMsg = errorMessage
                    isResending = false
                    resent(false)
                }
            }
        }
    }

    private fun clearAllAppData() {
        fhirAppDatabase.clearAllTables()
        preferenceRepository.clearPreferences()
    }

    private suspend fun stopWorkers() {
        WorkManager.getInstance(getApplication<Application>().applicationContext)
            .cancelAllWork().await().also {
                (getApplication<FhirApp>().applicationContext.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler).cancelAll()
            }
    }
}