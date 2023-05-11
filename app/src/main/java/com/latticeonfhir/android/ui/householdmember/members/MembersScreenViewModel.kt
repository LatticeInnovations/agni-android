package com.latticeonfhir.android.ui.householdmember.members

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.repository.patient.PatientRepository
import com.latticeonfhir.android.data.local.repository.relation.RelationRepository
import com.latticeonfhir.android.data.local.roomdb.entities.RelationEntity
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.data.server.model.patient.PatientResponseWithRelation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MembersScreenViewModel @Inject constructor(
    private val relationRepository: RelationRepository,
    private val patientRepository: PatientRepository
) : BaseViewModel() {
    var loading by mutableStateOf(true)
    var relationsList by mutableStateOf(listOf<RelationEntity>())
    var relationsListWithRelation by mutableStateOf(listOf<PatientResponseWithRelation>())

    internal fun getAllRelations(patientId: String) {
        viewModelScope.launch {
            relationsList = relationRepository.getAllRelationOfPatient(patientId)
            relationsList.forEach { relation ->
                val patientResponseWithRelation = getPatientData(relation.toId)
                if (!relationsListWithRelation.contains(
                        PatientResponseWithRelation(
                            patientResponseWithRelation,
                            relation.relation
                        )
                    )
                ) {
                    relationsListWithRelation = relationsListWithRelation + listOf(
                        PatientResponseWithRelation(
                            patientResponseWithRelation,
                            relation.relation
                        )
                    )
                }

            }
            loading = false
            Timber.d("manseeyy ${relationsListWithRelation.size} $relationsListWithRelation")
        }
    }

    internal suspend fun getPatientData(id: String): PatientResponse {
        return patientRepository.getPatientById(id)[0]
    }
}