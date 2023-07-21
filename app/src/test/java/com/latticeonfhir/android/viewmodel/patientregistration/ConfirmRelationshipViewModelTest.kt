package com.latticeonfhir.android.viewmodel.patientregistration

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.latticeonfhir.android.base.BaseClass
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.patient.PatientRepository
import com.latticeonfhir.android.data.local.repository.relation.RelationRepository
import com.latticeonfhir.android.ui.patientregistration.step4.ConfirmRelationshipViewModel
import com.latticeonfhir.android.utils.MainCoroutineRule
import com.latticeonfhir.android.utils.converters.responseconverter.toRelation
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class ConfirmRelationshipViewModelTest: BaseClass() {
    @Mock
    lateinit var patientRepository: PatientRepository
    @Mock
    lateinit var relationRepository: RelationRepository
    @Mock
    lateinit var genericRepository: GenericRepository

    lateinit var viewModel: ConfirmRelationshipViewModel

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    public override fun setUp(){
        MockitoAnnotations.openMocks(this)
        viewModel = ConfirmRelationshipViewModel(relationRepository, patientRepository, genericRepository)
    }

    @Test
    fun getPatientDataTest() = runBlocking {
        `when`(patientRepository.getPatientById(viewModel.patientId)).thenReturn(listOf(patientResponse))

        viewModel.getPatientData(viewModel.patientId) {
            Assert.assertEquals(patientResponse, it)
        }
    }

    @Test
    fun getRelationBetweenTest() = runBlocking {
        `when`(relationRepository.getRelationBetween(id, relativeId)).thenReturn(
            listOf(relationView)
        )
        viewModel.getRelationBetween(id, relativeId)
        delay(1000)
        Assert.assertEquals(viewModel.relationBetween, listOf(relationView))
    }

    @Test
    fun deleteRelationTest() = runBlocking {
        `when`(relationRepository.deleteRelation(id, relativeId)).thenReturn(1)
        `when`(relationRepository.getRelationBetween(id, relativeId)).thenReturn(
            listOf(relationView)
        )
        viewModel.deleteRelation(id, relativeId)
        delay(1000)
        Assert.assertEquals(viewModel.relationBetween, listOf(relationView))
    }

    @Test
    fun `discard relations`() = runBlocking {
        val result = viewModel.discardRelations()
        delay(5000)
        assertEquals(Unit,result)
    }

    @Test
    fun `update relations`() = runBlocking {
        `when`(relationRepository.updateRelation(eq(relationEntity.toRelation()) , any())).thenAnswer { invocation ->
            val callback = invocation.getArgument<(Int) -> Unit>(1)
            callback.invoke(1)
        }
        `when`(relationRepository.getRelationBetween(viewModel.patientId, viewModel.relativeId)).thenReturn(
            listOf(relationView)
        )
        viewModel.updateRelation(relationEntity.toRelation())

        delay(1000)
        assertEquals(viewModel.relationBetween, listOf(relationView))
    }

    @Test
    fun `add relation to genericEntity`() = runBlocking  {
        viewModel.relationBetween = listOf(relationView)
        val result = viewModel.addRelationsToGenericEntity()
        delay(5000)
        assertEquals(Unit,result)
    }
}