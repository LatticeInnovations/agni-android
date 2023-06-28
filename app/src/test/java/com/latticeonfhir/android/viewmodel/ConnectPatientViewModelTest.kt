package com.latticeonfhir.android.viewmodel

import com.latticeonfhir.android.base.BaseClass
import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.relation.RelationRepository
import com.latticeonfhir.android.data.server.model.relatedperson.RelatedPersonResponse
import com.latticeonfhir.android.data.server.model.relatedperson.Relationship
import com.latticeonfhir.android.ui.householdmember.connectpatient.ConnectPatientViewModel
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.eq
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import com.nhaarman.mockitokotlin2.any
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class ConnectPatientViewModelTest : BaseClass() {
    @Mock
    lateinit var relationRepository: RelationRepository

    @Mock
    lateinit var genericRepository: GenericRepository

    lateinit var viewModel: ConnectPatientViewModel

    @Before
    public override fun setUp() {
        MockitoAnnotations.openMocks(this)
        viewModel = ConnectPatientViewModel(genericRepository, relationRepository)
    }

    @Test
    fun getRelationBetweenAndRemoveRelationBetweenTest() = runTest {
        `when`(relationRepository.getRelationBetween(patientResponse.id, relative.id)).thenReturn(
            listOf(relationView)
        )
        viewModel.getRelationBetween(patientResponse.id, relative.id)
        delay(2000)
        assertEquals(listOf(relationView), viewModel.connectedMembersList)

        viewModel.removeRelationBetween(patientResponse.id, relative.id) {
            assertEquals(listOf(relationView), viewModel.connectedMembersList)
        }
    }

    @Test
    fun discardRelationsTest() = runTest {
        `when`(relationRepository.deleteRelation(*viewModel.connectedMembersList.map { it.id }
            .toTypedArray())).thenReturn(1)
        viewModel.discardRelations()
    }

    @Test
    fun deleteRelationTest() = runTest {
        `when`(relationRepository.deleteRelation(patientResponse.id, relative.id)).thenReturn(1)
        viewModel.deleteRelation(patientResponse.id, relative.id) {
            assertEquals(1, it)
        }
    }

    @Test
    fun updateRelationTest() = runBlocking {
        `when`(relationRepository.updateRelation(eq(relation), anyOrNull())).then { invocation ->
            val callback = invocation.getArgument<(Int) -> Unit>(1)
            callback.invoke(1)
        }
        `when`(relationRepository.getRelationBetween(patientResponse.id, relative.id)).thenReturn(
            listOf(relationView)
        )
        viewModel.updateRelation(relation)
        delay(2000)
        assertEquals(listOf(relationView), viewModel.connectedMembersList.toList())
    }

    @Test
    fun addRelationTest() = runTest {
        `when`(relationRepository.addRelation(eq(relation), any())).thenAnswer { invocation ->
            val callback = invocation.getArgument<(List<Long>) -> Unit>(1)
            callback.invoke(listOf(-1L))
        }
        viewModel.addRelation(relation) {
            assertEquals(listOf(-1L), it)
        }
    }

    @Test
    fun addRelationToGenericTest() = runBlocking {
        viewModel.connectedMembersList.add(relationView)
        `when`(
            genericRepository.insertOrUpdatePostEntity(
                patientId = relationView.patientId,
                entity = RelatedPersonResponse(
                    id = relationView.patientId,
                    relationship = listOf(
                        Relationship(
                            relativeId = relationView.relativeId,
                            patientIs = relationView.relation.value
                        )
                    )
                ),
                typeEnum = GenericTypeEnum.RELATION
            )
        ).thenReturn(-1L)
        viewModel.addRelationsToGenericEntity()
    }
}