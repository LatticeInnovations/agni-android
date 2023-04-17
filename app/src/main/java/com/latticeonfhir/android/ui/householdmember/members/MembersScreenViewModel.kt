package com.latticeonfhir.android.ui.householdmember.members

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.repository.patient.PatientRepository
import com.latticeonfhir.android.data.local.repository.relation.RelationRepository
import com.latticeonfhir.android.data.local.roomdb.entities.RelationEntity
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MembersScreenViewModel @Inject constructor(
    private val relationRepository: RelationRepository,
    private val patientRepository: PatientRepository
): BaseViewModel() {
    var relationsList = mutableStateListOf<RelationEntity>()

    internal fun getAllRelations(patientId: String){
        viewModelScope.launch {
            //relationsList.clear()
            relationsList.addAll(relationRepository.getAllRelationOfPatient(patientId))
        }
    }

    internal fun getPatientData(id: String, patientResponse: (PatientResponse) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            patientResponse(patientRepository.getPatientById(id)[0])
        }
    }
}