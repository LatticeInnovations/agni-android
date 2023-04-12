package com.latticeonfhir.android.ui.householdmember.members

import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.repository.patient.PatientRepository
import com.latticeonfhir.android.data.local.repository.relation.RelationRepository
import com.latticeonfhir.android.data.local.roomdb.entities.RelationEntity
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MembersScreenViewModel @Inject constructor(
    private val relationRepository: RelationRepository,
    private val patientRepository: PatientRepository
): BaseViewModel() {
    var membersList = mutableListOf<RelationEntity>()

    internal fun getAllRelations(patientId: String){
        viewModelScope.launch {
            membersList = relationRepository.getAllRelationOfPatient(patientId) as MutableList<RelationEntity>
        }
    }
}