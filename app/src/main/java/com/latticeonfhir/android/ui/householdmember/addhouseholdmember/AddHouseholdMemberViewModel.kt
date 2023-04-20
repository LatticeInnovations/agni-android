package com.latticeonfhir.android.ui.householdmember.addhouseholdmember

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.model.SearchParameters
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.relation.RelationRepository
import com.latticeonfhir.android.data.local.repository.search.SearchRepository
import com.latticeonfhir.android.data.local.roomdb.dao.PatientDao
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.LinkedList
import javax.inject.Inject

@HiltViewModel
class AddHouseholdMemberViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : BaseViewModel() {
    var isLaunched by mutableStateOf(false)
    var patient by mutableStateOf<PatientResponse?>(null)

    var showRelationDialogue by mutableStateOf(false)
    var listOfSuggestions = mutableListOf<PatientResponse>()
    var suggestedMembersList = mutableStateListOf<PatientResponse>()
    var selectedSuggestedMembersList = mutableStateListOf<PatientResponse>()

    internal fun getSuggestions(patient: PatientResponse, returnList: (List<PatientResponse>) -> Unit) {
        viewModelScope.launch(Dispatchers.Main) {
            searchRepository.getSuggestedMembers(
                patient.id,
                SearchParameters(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    patient.permanentAddress.addressLine1,
                    patient.permanentAddress.city,
                    patient.permanentAddress.district,
                    patient.permanentAddress.state,
                    patient.permanentAddress.postalCode,
                    patient.permanentAddress.addressLine2
                )
            ) {
                listOfSuggestions = it
                suggestedMembersList.clear()
                if (listOfSuggestions.size > 5) {
                    suggestedMembersList.add(listOfSuggestions[0])
                    suggestedMembersList.add(listOfSuggestions[1])
                    suggestedMembersList.add(listOfSuggestions[2])
                    suggestedMembersList.add(listOfSuggestions[3])
                    suggestedMembersList.add(listOfSuggestions[4])
                } else {
                    suggestedMembersList.addAll(listOfSuggestions)
                }
            }
            returnList(
                suggestedMembersList
            )
        }
    }
}