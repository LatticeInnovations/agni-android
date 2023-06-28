package com.latticeonfhir.android.viewmodel.patientregistration

import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.patient.PatientRepository
import com.latticeonfhir.android.data.local.repository.relation.RelationRepository
import com.latticeonfhir.android.base.BaseClass
import com.latticeonfhir.android.ui.patientregistration.step4.ConfirmRelationshipViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
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

    @Before
    public override fun setUp(){
        MockitoAnnotations.initMocks(this)
        viewModel = ConfirmRelationshipViewModel(relationRepository, patientRepository, genericRepository)
    }

    @Test
    fun getPatientDataTest() = runBlocking {
        `when`(patientRepository.getPatientById(viewModel.patientId)).thenReturn(listOf(patientResponse))

        viewModel.getPatientData(viewModel.patientId){
            Assert.assertEquals(patientResponse, it)
        }
    }

    @Test
    fun getRelationBetweenTest() = runTest {
        `when`(relationRepository.getRelationBetween(viewModel.patientId, viewModel.relativeId)).thenReturn(
            listOf(relationView)
        )
        viewModel.getRelationBetween(viewModel.patientId, viewModel.relativeId)
        delay(5000)
        Assert.assertEquals(listOf(relationView), viewModel.relationBetween)
    }

    @Test
    fun deleteRelationTest() = runTest {
        `when`(relationRepository.deleteRelation(viewModel.patientId, viewModel.relativeId)).thenReturn(1)
        val result = viewModel.deleteRelation(viewModel.patientId, viewModel.relativeId)
        delay(5000)
        Assert.assertEquals(listOf(relationView), viewModel.relationBetween)
    }
}