package com.latticeonfhir.android.ui.householdmember.suggestions

import android.util.Log
import androidx.compose.runtime.getValue
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
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.SynchronousQueue
import javax.inject.Inject

@HiltViewModel
class SuggestionsScreenViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    private val genericRepository: GenericRepository,
    private val relationRepository: RelationRepository,
    private val patientDao: PatientDao
    ) :
    BaseViewModel() {
    var showConnectDialog by mutableStateOf(false)

    private val queue = ConcurrentLinkedQueue<PatientResponse>()
    private var i = 0

    //private lateinit var listOfSuggestions: List<PatientResponse>
    var listOfSuggestions by mutableStateOf(listOf<PatientResponse>())
    var suggestedMembersList by mutableStateOf(listOf<PatientResponse>())

    internal fun updateQueue() {
        queue.poll()
        if (listOfSuggestions.isNotEmpty() && listOfSuggestions.size > i) {
            queue.offer(listOfSuggestions[i])
            i++
        }
        suggestedMembersList = queue.toList()
    }


    internal fun getQueueItems(patient: PatientResponse)  {
        viewModelScope.launch(Dispatchers.IO) {
            listOfSuggestions = searchRepository.getSuggestedMembers(
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
            )
        }
        if (listOfSuggestions.isNotEmpty()) {
            while (listOfSuggestions.size > i) {
                queue.add(
                    listOfSuggestions[i]
                )
                i++
                if(queue.size == 5) break
            }
            suggestedMembersList = queue.toList()
        }
    }

    fun addRelation(relation: Relation, relativeId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            RelationConverter.getInverseRelation(relation.toRelationEntity(), patientDao) {
                viewModelScope.launch(Dispatchers.IO) {
                    genericRepository.insertOrUpdatePostEntity(
                        patientId = relation.patientId,
                        entity = RelatedPersonResponse(
                            id = relation.patientId,
                            relationship = listOf(
                                Relationship(
                                    patientIs = RelationConverter.getRelationEnumFromString(relation.relation),
                                    relativeId = relativeId,
                                    relativeIs = it.value
                                )
                            )
                        ),
                        typeEnum = GenericTypeEnum.RELATION
                    )
                }
            }
            relationRepository.addRelation(relation)
        }
    }
}