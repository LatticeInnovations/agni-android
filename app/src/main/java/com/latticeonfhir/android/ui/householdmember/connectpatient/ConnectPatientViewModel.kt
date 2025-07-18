package com.latticeonfhir.android.ui.householdmember.connectpatient

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.model.relation.Relation
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.relation.RelationRepository
import com.latticeonfhir.android.data.local.roomdb.views.RelationView
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.data.server.model.relatedperson.RelatedPersonResponse
import com.latticeonfhir.android.data.server.model.relatedperson.Relationship
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ConnectPatientViewModel @Inject constructor(
    private val genericRepository: GenericRepository,
    private val relationRepository: RelationRepository
) : BaseViewModel() {
    var isLaunched by mutableStateOf(false)

    var discardAllRelationDialog by mutableStateOf(false)
    var showConfirmDialog by mutableStateOf(false)

    var patientFrom by mutableStateOf<PatientResponse?>(null)
    var selectedMembersList = mutableListOf<PatientResponse?>(null)
    var connectedMembersList = mutableStateListOf<RelationView>()
    var membersList = mutableStateListOf(*selectedMembersList.toTypedArray())

    internal fun getRelationBetween(
        patientId: String,
        relativeId: String,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            connectedMembersList.addAll(
                relationRepository.getRelationBetween(
                    patientId,
                    relativeId
                )
            )
            Timber.d("manseeyy added to connected patients : ${connectedMembersList.toList()}")
        }
    }

    internal fun removeRelationBetween(
        patientId: String,
        relativeId: String,
        removed: (Boolean) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            removed(
                connectedMembersList.removeAll(
                    relationRepository.getRelationBetween(
                        patientId,
                        relativeId
                    )
                )
            )
        }
    }

    internal fun discardRelations() {
        viewModelScope.launch(Dispatchers.IO) {
            relationRepository.deleteRelation(*connectedMembersList.map { it.id }.toTypedArray())
        }
    }

    internal fun deleteRelation(fromId: String, toId: String, relationDeleted: (Int) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            relationDeleted(
                relationRepository.deleteRelation(
                    fromId,
                    toId
                )
            )
        }
    }

    internal fun updateRelation(relation: Relation) {
        viewModelScope.launch(Dispatchers.IO) {
            relationRepository.updateRelation(relation) {
                if (it > 0) {
                    getRelationBetween(relation.patientId, relation.relativeId)
                }
            }
        }
    }

    fun addRelation(relation: Relation, relationAdded: (List<Long>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            relationRepository.addRelation(relation, relationAdded)
        }
    }

    internal fun addRelationsToGenericEntity() {
        viewModelScope.launch(Dispatchers.IO) {
            connectedMembersList.forEach { relationView ->
                genericRepository.insertRelation(
                    relationView.patientId,
                    RelatedPersonResponse(
                        id = relationView.patientFhirId ?: relationView.patientId,
                        relationship = listOf(
                            Relationship(
                                relativeId = relationView.relativeFhirId ?: relationView.relativeId,
                                patientIs = relationView.relation.value
                            )
                        )
                    )
                )
            }
        }
    }
}