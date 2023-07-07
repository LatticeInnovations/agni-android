package com.latticeonfhir.android.viewmodel

import com.latticeonfhir.android.data.local.model.relation.Relation
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.relation.RelationRepository
import com.latticeonfhir.android.data.local.repository.search.SearchRepository
import com.latticeonfhir.android.data.local.roomdb.dao.PatientDao
import com.latticeonfhir.android.base.BaseClass
import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.model.search.SearchParameters
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.data.server.model.relatedperson.RelatedPersonResponse
import com.latticeonfhir.android.data.server.model.relatedperson.Relationship
import com.latticeonfhir.android.ui.householdmember.suggestions.SuggestionsScreenViewModel
import com.latticeonfhir.android.utils.converters.responseconverter.toPatientAndIdentifierEntityResponse
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.*
import net.bytebuddy.dynamic.scaffold.MethodGraph.Linked
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.util.LinkedList

@OptIn(ExperimentalCoroutinesApi::class)
class SuggestionsScreenViewModelTest : BaseClass() {
    @Mock
    lateinit var patientDao: PatientDao

    @Mock
    lateinit var relationRepository: RelationRepository

    @Mock
    lateinit var genericRepository: GenericRepository

    @Mock
    lateinit var searchRepository: SearchRepository
    lateinit var viewModel: SuggestionsScreenViewModel

    @Before
    public override fun setUp() {
        MockitoAnnotations.openMocks(this)
        viewModel = SuggestionsScreenViewModel(
            searchRepository,
            genericRepository,
            relationRepository,
            patientDao
        )
    }

    @Test
    fun updateQueueLessThan5Test() {
        viewModel.listOfSuggestions = mutableListOf(relative, patientResponse)
        viewModel.updateQueue(patientResponse)
        assertEquals(listOf(relative), viewModel.listOfSuggestions)
    }

    @Test
    fun updateQueueMoreThan5Test() {
        viewModel.listOfSuggestions = mutableListOf(relative, patientResponse, relative, relative, relative, relative, relative)
        viewModel.updateQueue(patientResponse)
        assertEquals(listOf(relative, relative, relative, relative, relative, relative), viewModel.listOfSuggestions)
    }

    @Test
    fun getQueueItemsLessThan5Test() = runBlocking {
        `when`(
            searchRepository.getSuggestedMembers(
                eq(patientResponse.id),
                eq(SearchParameters(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    patientResponse.permanentAddress.addressLine1,
                    patientResponse.permanentAddress.city,
                    patientResponse.permanentAddress.district,
                    patientResponse.permanentAddress.state,
                    patientResponse.permanentAddress.postalCode,
                    patientResponse.permanentAddress.addressLine2
                )),
                any()
            )
        ).thenAnswer { invocation ->
            val callback = invocation.getArgument<(LinkedList<PatientResponse>) -> Unit>(2)
            callback.invoke(LinkedList(listOf(relative)))
        }
        viewModel.getQueueItems(patientResponse)
        delay(2000)
        assertEquals(listOf(relative), viewModel.listOfSuggestions)
    }

    @Test
    fun getQueueItemsMoreThan5Test() = runBlocking {
        `when`(
            searchRepository.getSuggestedMembers(
                eq(patientResponse.id),
                eq(SearchParameters(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    patientResponse.permanentAddress.addressLine1,
                    patientResponse.permanentAddress.city,
                    patientResponse.permanentAddress.district,
                    patientResponse.permanentAddress.state,
                    patientResponse.permanentAddress.postalCode,
                    patientResponse.permanentAddress.addressLine2
                )),
                any()
            )
        ).thenAnswer { invocation ->
            val callback = invocation.getArgument<(LinkedList<PatientResponse>) -> Unit>(2)
            callback.invoke(LinkedList(listOf(relative, relative, relative, relative, relative, relative)))
        }
        viewModel.getQueueItems(patientResponse)
        delay(2000)
        assertEquals(listOf(relative, relative, relative, relative, relative, relative), viewModel.listOfSuggestions)
    }

    @Test
    fun addRelationTest() = runTest {
        `when`(patientDao.getPatientDataById(relationEntityBrother.fromId)).thenReturn(
            listOf(
                patientResponse.toPatientAndIdentifierEntityResponse()
            )
        )
        `when`(patientDao.getPatientDataById(relationEntityBrother.toId)).thenReturn(listOf(relative.toPatientAndIdentifierEntityResponse()))
        `when`(
            relationRepository.addRelation(
                eq(relationBrother),
                any()
            )
        ).thenAnswer { invocation ->
            val callback = invocation.getArgument<(List<Long>) -> Unit>(1)
            callback.invoke(listOf(-1L))
        }
        `when`(
            genericRepository.insertOrUpdatePostEntity(
                patientId = relationBrother.patientId,
                entity = RelatedPersonResponse(
                    id = relationBrother.patientId,
                    relationship = listOf(
                        Relationship(
                            patientIs = relationBrother.relation,
                            relativeId = relativeId
                        )
                    )
                ),
                typeEnum = GenericTypeEnum.RELATION
            )
        ).thenReturn(-1L)
        `when`(
            genericRepository.insertOrUpdatePostEntity(
                patientId = relativeId,
                entity = RelatedPersonResponse(
                    id = relativeId,
                    relationship = listOf(
                        Relationship(
                            patientIs = relationBrother.relation,
                            relativeId = relationBrother.patientId
                        )
                    )
                ),
                typeEnum = GenericTypeEnum.RELATION
            )
        ).thenReturn(-1L)
        viewModel.addRelation(relationBrother, relativeId) {
            assertEquals(listOf(-1L), it)
        }
    }
}