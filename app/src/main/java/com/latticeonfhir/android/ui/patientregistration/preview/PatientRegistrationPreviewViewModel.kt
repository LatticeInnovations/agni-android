package com.latticeonfhir.android.ui.patientregistration.preview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.identifier.IdentifierRepository
import com.latticeonfhir.android.data.local.repository.patient.PatientRepository
import com.latticeonfhir.android.data.local.repository.relation.RelationRepository
import com.latticeonfhir.android.data.local.roomdb.dao.PatientDao
import com.latticeonfhir.android.data.server.model.patient.PatientIdentifier
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.data.server.model.relatedperson.RelatedPersonResponse
import com.latticeonfhir.android.data.server.model.relatedperson.Relationship
import com.latticeonfhir.android.data.server.repository.sync.SyncRepository
import com.latticeonfhir.android.ui.patientregistration.step3.Address
import com.latticeonfhir.android.utils.converters.responseconverter.toPatientEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toRelationEntity
import com.latticeonfhir.android.utils.relation.Relation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PatientRegistrationPreviewViewModel @Inject constructor(
    private val syncRepository: SyncRepository,
    private val patientRepository: PatientRepository,
    private val genericRepository: GenericRepository,
    private val identifierRepository: IdentifierRepository,
    private val relationRepository: RelationRepository,
    private val patientDao: PatientDao
): BaseViewModel(), DefaultLifecycleObserver {

    var firstName by mutableStateOf("")
    var middleName by mutableStateOf("")
    var lastName by mutableStateOf("")
    var phoneNumber by mutableStateOf("")
    var email by mutableStateOf("")
    var dob by mutableStateOf("")
    var dobDay by mutableStateOf("")
    var dobMonth by mutableStateOf("")
    var dobYear by mutableStateOf("")
    var years by mutableStateOf("")
    var months by mutableStateOf("")
    var days by mutableStateOf("")
    var gender by mutableStateOf("")
    var passportId by mutableStateOf("")
    var voterId by mutableStateOf("")
    var patientId by mutableStateOf("")

    var homeAddress by mutableStateOf(Address())
    var workAddress by mutableStateOf(Address())

    var openDialog by mutableStateOf(false)
    val identifierList = mutableListOf<PatientIdentifier>()

    var fromHouseholdMember by mutableStateOf(false)
    var patientFrom by mutableStateOf<PatientResponse?>(null)
    var patientFromId by mutableStateOf("")
    var relativeId by mutableStateOf("")
    var relation by mutableStateOf("")

    fun addPatient(patientResponse : PatientResponse){
        viewModelScope.launch(Dispatchers.IO) {
            patientRepository.addPatient(patientResponse)
            genericRepository.insertOrUpdatePostEntity(
                patientId = patientResponse.id,
                entity = patientResponse,
                typeEnum = GenericTypeEnum.PATIENT
            )
            identifierRepository.insertIdentifierList(patientResponse)
        }
    }

    fun addRelation(relationship: Relationship){
        viewModelScope.launch(Dispatchers.IO) {
            Relation.getInverseRelation(relationship.toRelationEntity(), patientDao){
                viewModelScope.launch(Dispatchers.IO) {
                    genericRepository.insertOrUpdatePostEntity(
                        patientId = relationship.patientId,
                        entity = RelatedPersonResponse(
                            id = relationship.patientId,
                            relationship = listOf(Relationship(
                                patientId = Relation.getRelationEnumFromString(relation),
                                relativeId = relativeId,
                                relation = it.value
                            ))
                        ),
                        typeEnum = GenericTypeEnum.RELATION
                    )
                }
            }
            relationRepository.addRelation(relationship)
        }
    }
}