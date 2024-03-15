package com.latticeonfhir.android.ui.householdmember.searchresult

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.google.android.fhir.logicalId
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.model.search.SearchParameters
import com.latticeonfhir.android.data.local.repository.search.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Patient
import javax.inject.Inject

@HiltViewModel
class SearchResultViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : BaseViewModel() {
    var isLaunched by mutableStateOf(false)

    var patientFrom by mutableStateOf(Patient())
    var searchResultList: Flow<PagingData<Patient>> by mutableStateOf(flowOf())
    var searchParameters by mutableStateOf<SearchParameters?>(null)
    var selectedMembersList = mutableStateListOf<Patient>()
    var size by mutableIntStateOf(0)

    internal fun searchPatient(searchParameters: SearchParameters) {
        viewModelScope.launch(Dispatchers.IO) {
            searchResultList =
                searchRepository.filteredSearchPatients(
                    patientFrom.logicalId,
                    searchParameters,
                    searchRepository.getSearchListFhir()
                ).map { data ->
                    data.map { paginationResponse ->
                        if (size == 0) size = paginationResponse.size
                        paginationResponse.data
                    }
                }.cachedIn(viewModelScope)
        }
    }
}