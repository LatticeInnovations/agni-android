package com.latticeonfhir.android.ui.householdmember.searchresult

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.model.search.SearchParameters
import com.latticeonfhir.android.data.local.repository.search.SearchRepository
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchResultViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : BaseViewModel() {
    var isLaunched by mutableStateOf(false)

    var patientFrom by mutableStateOf<PatientResponse?>(null)
    var searchResultList: Flow<PagingData<PatientResponse>> by mutableStateOf(flowOf<PagingData<PatientResponse>>())
    var searchParameters by mutableStateOf<SearchParameters?>(null)
    var selectedMembersList = mutableStateListOf<PatientResponse>()
    var size by mutableStateOf(0)

    internal fun searchPatient(searchParameters: SearchParameters) {
        viewModelScope.launch(Dispatchers.IO) {
            searchResultList =
                searchRepository.filteredSearchPatients(patientFrom?.id!!, searchParameters).map {
                    it.map {
                        if (size == 0) size = it.size
                        it.data
                    }
                }.cachedIn(viewModelScope)
        }
    }
}