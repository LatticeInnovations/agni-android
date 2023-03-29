package com.latticeonfhir.android.ui.main.landingscreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.repository.patient.PatientRepository
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LandingScreenViewModel @Inject constructor(
    private val patientRepository: PatientRepository
) : BaseViewModel() {

    val items = listOf("My Patients", "Queue", "Profile")
    var isSearching by mutableStateOf(false)
    var searchQuery by mutableStateOf("")
    var selectedIndex by mutableStateOf(0)
    lateinit var patientList: Flow<PagingData<PatientResponse>>

    init {
        viewModelScope.launch {
            patientList = patientRepository.getPatientList().asFlow().cachedIn(viewModelScope)
        }
    }
}