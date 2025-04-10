package com.latticeonfhir.core.ui.householdmember.suggestions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.core.base.viewmodel.BaseViewModel
import com.latticeonfhir.core.data.local.model.relation.Relation
import com.latticeonfhir.core.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.relation.RelationRepository
import com.latticeonfhir.android.data.local.repository.search.SearchRepository
import com.latticeonfhir.core.data.local.roomdb.dao.PatientDao
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.core.data.server.model.relatedperson.RelatedPersonResponse
import com.latticeonfhir.android.data.server.model.relatedperson.Relationship
import com.latticeonfhir.core.utils.builders.UUIDBuilder
import com.latticeonfhir.core.utils.converters.responseconverter.RelationConverter
import com.latticeonfhir.android.utils.converters.responseconverter.toRelationEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SuggestionsScreenViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    private val genericRepository: GenericRepository,
    private val relationRepository: RelationRepository,
    private val patientDao: PatientDao
) : com.latticeonfhir.core.base.viewmodel.BaseViewModel() {
    var loading by mutableStateOf(true)
    var membersList by mutableStateOf(listOf<PatientResponse>())

    internal fun getQueueItems(patient: PatientResponse) {
        viewModelScope.launch(Dispatchers.IO) {
            membersList = searchRepository.getFiveSuggestedMembers(
                patient.id,
                patient.permanentAddress
            )
            loading = false
        }
    }

    fun addRelation(
        relation: Relation,
        relativeId: String,
        genericUUID: String = UUIDBuilder.generateUUID(),
        genericUUIDInverse: String = UUIDBuilder.generateUUID(),
        relationAdded: (List<Long>) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            RelationConverter.getInverseRelation(
                relation.toRelationEntity(),
                patientDao
            ) { inverseRelation ->
                viewModelScope.launch(Dispatchers.IO) {
                    genericRepository.insertRelation(
                        relation.patientId,
                        RelatedPersonResponse(
                            id = relation.patientId,
                            relationship = listOf(
                                Relationship(
                                    patientIs = relation.relation,
                                    relativeId = relativeId
                                )
                            )
                        ),
                        genericUUID
                    ).also {
                        genericRepository.insertRelation(
                            relativeId,
                            RelatedPersonResponse(
                                id = relativeId,
                                relationship = listOf(
                                    Relationship(
                                        patientIs = inverseRelation.value,
                                        relativeId = relation.patientId
                                    )
                                )
                            ),
                            genericUUIDInverse
                        )
                    }
                }
            }
            relationRepository.addRelation(relation, relationAdded)
        }
    }
}