package com.latticeonfhir.android.ui.householdmember.members

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.SearchResult
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.utils.fhirengine.FhirQueries.getPersonResource
import com.latticeonfhir.android.utils.fhirengine.FhirQueries.getRelatedPerson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.RelatedPerson
import javax.inject.Inject

@HiltViewModel
class MembersScreenViewModel @Inject constructor(
    private val fhirEngine: FhirEngine
) : BaseViewModel() {
    var loading by mutableStateOf(true)
    var relationsListWithRelation by mutableStateOf(listOf<SearchResult<RelatedPerson>>())

    internal fun getAllRelations(patientId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val person = getPersonResource(fhirEngine, patientId)
            person.link.forEach { relatedPersonLink ->
                if (relatedPersonLink.target.reference.contains("RelatedPerson")) {
                    getRelatedPerson(
                        fhirEngine,
                        relatedPersonLink.target.reference.substringAfter("/")
                    ).forEach { result ->
                        relationsListWithRelation = relationsListWithRelation + listOf(result)
                    }
                }
            }
            loading = false
        }
    }
}