package com.heartcare.agni.ui.householdmember.members

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.local.model.patient.PatientResponseWithRelation
import com.heartcare.agni.data.local.repository.patient.PatientRepository
import com.heartcare.agni.data.local.repository.relation.RelationRepository
import com.heartcare.agni.data.local.roomdb.entities.relation.RelationEntity
import com.heartcare.agni.data.server.model.patient.PatientResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MembersScreenViewModel @Inject constructor(
    private val relationRepository: RelationRepository,
    private val patientRepository: PatientRepository
) : BaseViewModel() {
    var loading by mutableStateOf(true)
    var relationsList by mutableStateOf(listOf<RelationEntity>())
    private var relativesIdList = mutableSetOf<String>()
    var relationsListWithRelation by mutableStateOf(listOf<PatientResponseWithRelation>())

    internal fun getAllRelations(patientId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            relationsList = relationRepository.getAllRelationOfPatient(patientId)
            relationsList.forEach { relation ->
                val patientResponseWithRelation = getPatientData(relation.toId)
                if (!relativesIdList.contains(
                        patientResponseWithRelation.id
                    )
                ) {
                    relativesIdList.add(patientResponseWithRelation.id)
                    relationsListWithRelation = relationsListWithRelation + listOf(
                        PatientResponseWithRelation(
                            patientResponseWithRelation,
                            relation.relation
                        )
                    )
                }

            }
            loading = false
        }
    }

    private suspend fun getPatientData(id: String): PatientResponse {
        return patientRepository.getPatientById(id)[0]
    }
}