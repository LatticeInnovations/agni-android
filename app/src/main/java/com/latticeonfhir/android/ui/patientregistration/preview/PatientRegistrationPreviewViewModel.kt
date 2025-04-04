package com.latticeonfhir.android.ui.patientregistration.preview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.model.relation.Relation
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.identifier.IdentifierRepository
import com.latticeonfhir.android.data.local.repository.patient.PatientRepository
import com.latticeonfhir.android.data.local.repository.patient.lastupdated.PatientLastUpdatedRepository
import com.latticeonfhir.android.data.local.repository.relation.RelationRepository
import com.latticeonfhir.android.data.server.model.patient.PatientIdentifier
import com.latticeonfhir.android.data.server.model.patient.PatientLastUpdatedResponse
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.ui.patientregistration.step3.Address
import com.latticeonfhir.android.utils.builders.UUIDBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class PatientRegistrationPreviewViewModel @Inject constructor(
    private val patientRepository: PatientRepository,
    private val genericRepository: GenericRepository,
    private val identifierRepository: IdentifierRepository,
    private val relationRepository: RelationRepository,
    private val patientLastUpdatedRepository: PatientLastUpdatedRepository
) : com.latticeonfhir.android.base.viewmodel.BaseViewModel() {
    var isLaunched by mutableStateOf(false)
    var patientResponse by mutableStateOf<PatientResponse?>(null)

    internal var firstName by mutableStateOf("")
    internal var middleName by mutableStateOf("")
    internal var lastName by mutableStateOf("")
    internal var phoneNumber by mutableStateOf("")
    internal var email by mutableStateOf("")
    internal var dob by mutableStateOf("")
    internal var dobDay by mutableStateOf("")
    internal var dobMonth by mutableStateOf("")
    internal var dobYear by mutableStateOf("")
    internal var years by mutableStateOf("")
    internal var months by mutableStateOf("")
    internal var days by mutableStateOf("")
    internal var gender by mutableStateOf("")
    internal var passportId by mutableStateOf("")
    internal var voterId by mutableStateOf("")
    internal var patientId by mutableStateOf("")

    internal var homeAddress by mutableStateOf(Address())
    internal var workAddress by mutableStateOf(Address())

    internal var openDialog by mutableStateOf(false)
    internal val identifierList = mutableListOf<PatientIdentifier>()

    internal var fromHouseholdMember by mutableStateOf(false)
    internal var patientFrom by mutableStateOf<PatientResponse?>(null)
    internal var patientFromId by mutableStateOf("")
    internal var relativeId by mutableStateOf(UUIDBuilder.generateUUID())
    internal var relation by mutableStateOf("")

    internal fun addPatient(patientResponse: PatientResponse) {
        viewModelScope.launch(Dispatchers.IO) {
            patientRepository.addPatient(patientResponse)
            genericRepository.insertPatient(
                patientResponse
            )
            identifierRepository.insertIdentifierList(patientResponse)

            val patientLastUpdatedResponse = PatientLastUpdatedResponse(
                uuid = patientResponse.id,
                timestamp = Date()
            )
            patientLastUpdatedRepository.insertPatientLastUpdatedData(patientLastUpdatedResponse)
            genericRepository.insertPatientLastUpdated(patientLastUpdatedResponse)
        }
    }

    internal fun addRelation(relation: Relation, relationAdded: (List<Long>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            relationRepository.addRelation(relation, relationAdded)
        }
    }
}