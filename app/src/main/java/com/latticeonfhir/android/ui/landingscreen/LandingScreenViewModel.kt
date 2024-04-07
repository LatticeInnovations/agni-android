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
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.work.WorkManager
import androidx.work.await
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.sync.CurrentSyncJobStatus
import com.latticeonfhir.android.FhirApp
import com.latticeonfhir.android.base.viewmodel.BaseAndroidViewModel
import com.latticeonfhir.android.data.local.enums.UserRoleEnum
import com.latticeonfhir.android.data.local.model.search.SearchParameters
import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepository
import com.latticeonfhir.android.data.local.repository.search.SearchRepository
import com.latticeonfhir.android.service.workmanager.request.WorkRequestBuilders
import com.latticeonfhir.android.service.workmanager.utils.Delay
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.calculateMinutesToOneThirty
import com.latticeonfhir.android.utils.paging.PatientPagingSource
import com.latticeonfhir.android.utils.regex.LatticeIdRegex.LATTICE_ID_REGEX
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Patient
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class LandingScreenViewModel @Inject constructor(
    application: Application,
    private val fhirEngine: FhirEngine,
    private val searchRepository: SearchRepository,
    private val preferenceRepository: PreferenceRepository
) : BaseAndroidViewModel(application) {

    private val workRequestBuilders: WorkRequestBuilders by lazy { (application as FhirApp).workRequestBuilder }

    var isLaunched by mutableStateOf(false)
    var isLoading by mutableStateOf(true)
    val items = listOf("My Patients", "Queue", "Profile")
    var isSearching by mutableStateOf(false)
    var isSearchingByQuery by mutableStateOf(false)
    var isSearchResult by mutableStateOf(false)
    var searchQuery by mutableStateOf("")
    var selectedIndex by mutableIntStateOf(0)

    var patientList: Flow<PagingData<Patient>> by mutableStateOf(flowOf())
    var searchParameters by mutableStateOf<SearchParameters?>(null)
    var previousSearchList = mutableListOf<String>()
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

    init {

        if (getApplication<FhirApp>().periodicSyncJobStatus.value?.currentSyncJobStatus !is CurrentSyncJobStatus.Running) {
            viewModelScope.launch(Dispatchers.IO) {
                FhirApp.runEnqueuedWorker(application)
            }
        }

        getApplication<FhirApp>().periodicSyncJobStatus.observeForever {
            if(it.currentSyncJobStatus is CurrentSyncJobStatus.Succeeded) populateList()
        }

        viewModelScope.launch {
            getApplication<FhirApp>().sessionExpireFlow.asFlow().collectLatest { sessionExpireMap ->
                if (sessionExpireMap["errorReceived"] == true) {
                    logoutUser = true
                    logoutReason = sessionExpireMap["errorMsg"]?.toString() ?: "SERVER ERROR"
                }
            }
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
        userRole = UserRoleEnum.fromCode(preferenceRepository.getUserRoleId()).display
        userPhoneNo = preferenceRepository.getUserMobile().toString()
        userEmail = preferenceRepository.getUserEmail().run {
            if (isNullOrBlank()) "NA"
            else this
        }
    }

    private fun getPatientList() {
        viewModelScope.launch(Dispatchers.Main) {
            patientList = Pager(
                config = PagingConfig(pageSize = 20, enablePlaceholders = false),
                pagingSourceFactory = {
                    PatientPagingSource(
                        fhirEngine,
                        20,
                        isSearchResult,
                        searchParameters
                    )
                }
            ).flow.cachedIn(viewModelScope)
        }
    }

    fun populateList() {
        isLoading = true
        if (isSearchingByQuery) {
            if (LATTICE_ID_REGEX.matches(searchQuery)){
                searchParameters = SearchParameters(
                    searchQuery,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
                )
            } else {
                searchParameters = SearchParameters(
                    null,
                    searchQuery,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
                )
            }
        }
        getPatientList()
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