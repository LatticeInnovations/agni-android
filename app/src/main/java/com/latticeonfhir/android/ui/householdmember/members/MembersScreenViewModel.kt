package com.latticeonfhir.android.ui.householdmember.members

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.SearchResult
import com.google.android.fhir.logicalId
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.utils.fhirengine.FhirQueries.getPersonResource
import com.latticeonfhir.android.utils.fhirengine.FhirQueries.getRelatedPerson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.RelatedPerson
import org.hl7.fhir.r4.model.ResourceType
import javax.inject.Inject

@HiltViewModel
class MembersScreenViewModel @Inject constructor(
    private val fhirEngine: FhirEngine
) : BaseViewModel() {
    var loading by mutableStateOf(true)
    private var relationsIdList = mutableSetOf<String>()
    var relationsListWithRelation by mutableStateOf(listOf<SearchResult<RelatedPerson>>())

    internal fun getAllRelations(patientId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getPersonResource(fhirEngine, patientId).link.forEach { relatedPersonLink ->
                if (relatedPersonLink.target.reference.contains(ResourceType.RelatedPerson.name)) {
                    getRelatedPerson(
                        fhirEngine,
                        relatedPersonLink.target.reference.substringAfter("/")
                    ).forEach { result ->
                        relationsListWithRelation.forEach {
                            relationsIdList.add(it.resource.logicalId)
                        }
                        if (!relationsIdList.contains(result.resource.logicalId)) relationsListWithRelation = relationsListWithRelation + listOf(result)
                    }
                }
            }
            loading = false
        }
    }
}