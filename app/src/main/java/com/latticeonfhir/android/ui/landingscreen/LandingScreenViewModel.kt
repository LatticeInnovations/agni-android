package com.latticeonfhir.android.ui.landingscreen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.model.SearchParameters
import com.latticeonfhir.android.data.local.repository.patient.PatientRepository
import com.latticeonfhir.android.data.local.repository.search.SearchRepository
import com.latticeonfhir.android.data.server.model.patient.PatientAddressResponse
import com.latticeonfhir.android.data.server.model.patient.PatientIdentifier
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toPatientDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class LandingScreenViewModel @Inject constructor(
    private val patientRepository: PatientRepository,
    private val searchRepository: SearchRepository
) : BaseViewModel() {

    var isLaunched by mutableStateOf(false)
    val items = listOf("My Patients", "Queue", "Profile")
    var isSearching by mutableStateOf(false)
    var isSearchingByQuery by mutableStateOf(false)
    var isSearchResult by mutableStateOf(false)
    var searchQuery by mutableStateOf("")
    var selectedIndex by mutableStateOf(0)
    var patientList: Flow<PagingData<PatientResponse>> by mutableStateOf(flowOf<PagingData<PatientResponse>>())
    var searchResultList: Flow<PagingData<PatientResponse>> by mutableStateOf(flowOf<PagingData<PatientResponse>>())
    var searchParameters by mutableStateOf<SearchParameters?>(null)
    var previousSearchList = mutableListOf<String>()

    fun getPatientList() {
        viewModelScope.launch {
            patientList = patientRepository.getPatientList().asFlow().cachedIn(viewModelScope)
        }
    }

    fun populateList(){
        if(isSearchResult){
            if (isSearchingByQuery) searchPatientByQuery()
            else searchPatient(searchParameters!!)
        } else{
            getPatientList()
        }
    }

    internal fun getPreviousSearches(){
        viewModelScope.launch(Dispatchers.IO) {
            previousSearchList = searchRepository.getRecentSearches() as MutableList<String>
        }
    }

    internal fun insertRecentSearch(){
        viewModelScope.launch(Dispatchers.IO) {
            searchRepository.insertRecentSearch(searchQuery)
        }
    }

    internal fun searchPatient(searchParameters: SearchParameters) {
        viewModelScope.launch(Dispatchers.IO) {
            searchResultList = searchRepository.searchPatients(searchParameters).cachedIn(viewModelScope)
        }
    }

    internal fun searchPatientByQuery() {
        viewModelScope.launch(Dispatchers.IO) {
            searchResultList = searchRepository.searchPatientByQuery(searchQuery).cachedIn(viewModelScope)
        }
    }
}