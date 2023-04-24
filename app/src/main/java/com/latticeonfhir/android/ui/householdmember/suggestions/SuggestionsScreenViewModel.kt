package com.latticeonfhir.android.ui.householdmember.suggestions

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.model.Relation
import com.latticeonfhir.android.data.local.model.SearchParameters
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.relation.RelationRepository
import com.latticeonfhir.android.data.local.repository.search.SearchRepository
import com.latticeonfhir.android.data.local.roomdb.dao.PatientDao
import com.latticeonfhir.android.data.server.model.patient.PatientAddressResponse
import com.latticeonfhir.android.data.server.model.patient.PatientIdentifier
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.data.server.model.relatedperson.RelatedPersonResponse
import com.latticeonfhir.android.data.server.model.relatedperson.Relationship
import com.latticeonfhir.android.utils.converters.responseconverter.toRelationEntity
import com.latticeonfhir.android.utils.relation.RelationConverter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.SynchronousQueue
import javax.inject.Inject

@HiltViewModel
class SuggestionsScreenViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    private val genericRepository: GenericRepository,
    private val relationRepository: RelationRepository,
    private val patientDao: PatientDao
) : BaseViewModel() {
    var isLaunched by mutableStateOf(false)
    var loading by mutableStateOf(true)
    var listOfSuggestions = mutableListOf<PatientResponse>()
    var suggestedMembersList = mutableStateListOf<PatientResponse>()
    var membersList by mutableStateOf(listOf<PatientResponse>())

    internal fun updateQueue(patient: PatientResponse) {
        listOfSuggestions.remove(patient)
        suggestedMembersList.clear()
        if (listOfSuggestions.size > 5) {
            suggestedMembersList.add(listOfSuggestions[0])
            suggestedMembersList.add(listOfSuggestions[1])
            suggestedMembersList.add(listOfSuggestions[2])
            suggestedMembersList.add(listOfSuggestions[3])
            suggestedMembersList.add(listOfSuggestions[4])
        } else {
            suggestedMembersList.addAll(listOfSuggestions)
        }
        membersList = suggestedMembersList
    }


    internal fun getQueueItems(patient: PatientResponse) {
        viewModelScope.launch(Dispatchers.IO) {
            searchRepository.getSuggestedMembers(
                patient.id,
                SearchParameters(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    patient.permanentAddress.addressLine1,
                    patient.permanentAddress.city,
                    patient.permanentAddress.district,
                    patient.permanentAddress.state,
                    patient.permanentAddress.postalCode,
                    patient.permanentAddress.addressLine2
                )
            ) {
                listOfSuggestions = it
                suggestedMembersList.clear()
                if (listOfSuggestions.size > 5) {
                    suggestedMembersList.add(listOfSuggestions[0])
                    suggestedMembersList.add(listOfSuggestions[1])
                    suggestedMembersList.add(listOfSuggestions[2])
                    suggestedMembersList.add(listOfSuggestions[3])
                    suggestedMembersList.add(listOfSuggestions[4])
                } else {
                    suggestedMembersList.addAll(listOfSuggestions)
                }
                membersList = suggestedMembersList
                loading = false
            }
        }
    }

    fun addRelation(relation: Relation, relativeId: String, relationAdded: (List<Long>) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            RelationConverter.getInverseRelation(relation.toRelationEntity(), patientDao) {
                viewModelScope.launch(Dispatchers.IO) {
                    genericRepository.insertOrUpdatePostEntity(
                        patientId = relation.patientId,
                        entity = RelatedPersonResponse(
                            id = relation.patientId,
                            relationship = listOf(
                                Relationship(
                                    patientIs = relation.relation,
                                    relativeId = relativeId,
                                    relativeIs = it.value
                                )
                            )
                        ),
                        typeEnum = GenericTypeEnum.RELATION
                    )
                }
            }
            relationRepository.addRelation(relation, relationAdded)
        }
    }
}