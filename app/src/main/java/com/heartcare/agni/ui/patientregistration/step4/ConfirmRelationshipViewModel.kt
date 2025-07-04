package com.heartcare.agni.ui.patientregistration.step4

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.local.model.relation.Relation
import com.heartcare.agni.data.local.repository.generic.GenericRepository
import com.heartcare.agni.data.local.repository.patient.PatientRepository
import com.heartcare.agni.data.local.repository.relation.RelationRepository
import com.heartcare.agni.data.local.roomdb.views.RelationView
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.data.server.model.relatedperson.RelatedPersonResponse
import com.heartcare.agni.data.server.model.relatedperson.Relationship
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfirmRelationshipViewModel @Inject constructor(
    private val relationRepository: RelationRepository,
    private val patientRepository: PatientRepository,
    private val genericRepository: GenericRepository
) : BaseViewModel() {
    var isLaunched by mutableStateOf(false)

    var discardAllRelationDialog by mutableStateOf(false)

    var patientId by mutableStateOf("")
    var relativeId by mutableStateOf("")
    var relation by mutableStateOf("")

    var patient by mutableStateOf<PatientResponse?>(null)
    var relative by mutableStateOf<PatientResponse?>(null)
    var relationBetween by mutableStateOf(listOf<RelationView>())

    internal fun getPatientData(id: String, patientResponse: (PatientResponse) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            patientResponse(patientRepository.getPatientById(id)[0])
        }
    }

    internal fun getRelationBetween(
        patientId: String,
        relativeId: String,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            relationBetween = relationRepository.getRelationBetween(patientId, relativeId)
        }
    }

    internal fun deleteRelation(fromId: String, toId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            relationRepository.deleteRelation(
                fromId,
                toId
            )
            getRelationBetween(fromId, toId)
        }
    }

    internal fun discardRelations() {
        viewModelScope.launch(Dispatchers.IO) {
            relationRepository.deleteRelation(*relationBetween.map { it.id }.toTypedArray())
        }
    }

    internal fun updateRelation(relation: Relation) {
        viewModelScope.launch(Dispatchers.IO) {
            relationRepository.updateRelation(relation) {
                if (it > 0) {
                    getRelationBetween(patientId, relativeId)
                }
            }
        }
    }


    internal fun addRelationsToGenericEntity() {
        relationBetween.forEach { relationView ->
            viewModelScope.launch(Dispatchers.IO) {
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
