package com.latticeonfhir.features.household.ui.searchresult

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.latticeonfhir.core.base.viewmodel.BaseViewModel
import com.latticeonfhir.core.data.local.model.search.SearchParameters
import com.latticeonfhir.core.data.repository.local.appointment.AppointmentRepository
import com.latticeonfhir.core.data.repository.local.relation.RelationRepository
import com.latticeonfhir.core.data.repository.local.search.SearchRepository
import com.latticeonfhir.core.model.enums.LastVisit
import com.latticeonfhir.core.model.server.patient.PatientResponse
import com.latticeonfhir.core.utils.common.Queries.getSearchListWithLastVisited
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchResultViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    private val relationRepository: RelationRepository,
    private val appointmentRepository: AppointmentRepository
) : BaseViewModel() {
    var isLaunched by mutableStateOf(false)

    var patientFrom by mutableStateOf<PatientResponse?>(null)
    var searchResultList: Flow<PagingData<PatientResponse>> by mutableStateOf(flowOf())
    var searchParameters by mutableStateOf<SearchParameters?>(null)
    var selectedMembersList = mutableStateListOf<PatientResponse>()
    var size by mutableIntStateOf(0)

    internal fun searchPatient(searchParameters: SearchParameters) {
        viewModelScope.launch(Dispatchers.IO) {
            var finalSearchList = searchRepository.getSearchList()
            if (!searchParameters.lastFacilityVisit.isNullOrBlank() && searchParameters.lastFacilityVisit != LastVisit.NOT_APPLICABLE.label) {
                finalSearchList = getSearchListWithLastVisited(
                    searchParameters.lastFacilityVisit,
                    finalSearchList,
                    appointmentRepository
                )
            }
            searchResultList =
                searchRepository.filteredSearchPatients(
                    patientFrom?.id!!,
                    searchParameters,
                    finalSearchList,
                    relationRepository.getAllRelationOfPatient(patientFrom?.id!!).map { it.toId }
                        .toMutableSet()
                        .apply { add(patientFrom?.id!!) }
                ).map { data ->
                    data.map { paginationResponse ->
                        if (size == 0) size = paginationResponse.size
                        paginationResponse.data
                    }
                }.cachedIn(viewModelScope)
        }
    }
}