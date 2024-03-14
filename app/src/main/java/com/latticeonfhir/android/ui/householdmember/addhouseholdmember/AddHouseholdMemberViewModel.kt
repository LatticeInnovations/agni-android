package com.latticeonfhir.android.ui.householdmember.addhouseholdmember

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.google.android.fhir.logicalId
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.repository.search.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Patient
import javax.inject.Inject

@HiltViewModel
class AddHouseholdMemberViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : BaseViewModel() {
    var isLaunched by mutableStateOf(false)
    var patient by mutableStateOf(Patient())
    var searching by mutableStateOf(false)

    var showRelationDialogue by mutableStateOf(false)
    var suggestedMembersList by mutableStateOf(listOf<Patient>())
    var selectedSuggestedMembersList = mutableStateListOf<Patient>()

    internal fun getSuggestions(
        patient: Patient,
        coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
        returnList: (List<Patient>) -> Unit
    ) {
        viewModelScope.launch(coroutineDispatcher) {
            suggestedMembersList = searchRepository.getFiveSuggestedMembersFhir(
                patient.logicalId,
                patient.addressFirstRep
            )
            returnList(
                suggestedMembersList
            )
        }
    }
}