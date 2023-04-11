package com.latticeonfhir.android.ui.patientregistration.step4

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.model.RelationBetween
import com.latticeonfhir.android.data.local.repository.patient.PatientRepository
import com.latticeonfhir.android.data.local.repository.relation.RelationRepository
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.utils.relation.Relation.getStringFromRelationEnum
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfirmRelationshipViewModel @Inject constructor(
    private val relationRepository: RelationRepository,
    private val patientRepository: PatientRepository
) : BaseViewModel() {
    var openEditDialog by mutableStateOf(false)
    var openDeleteDialog by mutableStateOf(false)

    var showRelationCard by mutableStateOf(true)
    var showInverseRelationCard by mutableStateOf(true)

    var discardAllRelationDialog by mutableStateOf(false)

    var patientId by mutableStateOf("")
    var relativeId by mutableStateOf("")
    var relation by mutableStateOf("")

    var patient by mutableStateOf<PatientResponse?>(null)
    var relative by mutableStateOf<PatientResponse?>(null)
    var relationBetween by mutableStateOf<RelationBetween?>(null)

    internal fun getPatientData(id: String, patientResponse: (PatientResponse) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            patientResponse(patientRepository.getPatientById(id)[0])
        }
    }

    internal fun getRelationBetween(patientId: String, relativeId: String, relationBetween: (RelationBetween) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            relationBetween(relationRepository.getRelationBetween(
                patientId,
                relativeId
            ))
        }
    }

    internal fun deleteRelation(fromId: String, toId: String) {
        Log.d("manseeyy", fromId +" "+ toId +" relation deleted")
        viewModelScope.launch(Dispatchers.IO) {
            relationRepository.deleteRelation(
                fromId,
                toId
            )
        }
    }

    internal fun deleteAllRelation(patientId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            relationRepository.deleteAllRelationOfPatient(patientId)
        }
    }
}
