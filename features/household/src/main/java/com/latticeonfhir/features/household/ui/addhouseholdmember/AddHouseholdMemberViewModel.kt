package com.latticeonfhir.features.household.ui.addhouseholdmember

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.core.base.viewmodel.BaseViewModel
import com.latticeonfhir.core.data.repository.local.search.SearchRepository
import com.latticeonfhir.core.model.server.patient.PatientResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddHouseholdMemberViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : BaseViewModel() {
    var isLaunched by mutableStateOf(false)
    var patient by mutableStateOf<PatientResponse?>(null)

    var showRelationDialogue by mutableStateOf(false)
    var suggestedMembersList by mutableStateOf(listOf<PatientResponse>())
    var selectedSuggestedMembersList = mutableStateListOf<PatientResponse>()

    internal fun getSuggestions(
        patient: PatientResponse,
        coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
        returnList: (List<PatientResponse>) -> Unit
    ) {
        viewModelScope.launch(coroutineDispatcher) {
            suggestedMembersList = searchRepository.getFiveSuggestedMembers(
                patient.id,
                patient.permanentAddress
            )
            returnList(
                suggestedMembersList
            )
        }
    }
}