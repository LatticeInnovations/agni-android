package com.latticeonfhir.android.viewmodel.patientregistration

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.patient.PatientRepository
import com.latticeonfhir.android.data.local.repository.relation.RelationRepository
import com.latticeonfhir.android.base.BaseClass
import com.latticeonfhir.android.data.local.enums.RelationEnum
import com.latticeonfhir.android.data.local.roomdb.dao.RelationDao
import com.latticeonfhir.android.ui.patientregistration.step4.ConfirmRelationshipViewModel
import com.latticeonfhir.android.utils.MainCoroutineRule
import com.latticeonfhir.android.utils.converters.responseconverter.toRelation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
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
    @Mock
    private lateinit var relationDao: RelationDao

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
    fun getRelationBetweenTest() = runTest {
        `when`(relationRepository.getRelationBetween(viewModel.patientId, viewModel.relativeId)).thenReturn(
            listOf(relationView)
        )
        val result = viewModel.getRelationBetween(viewModel.patientId, viewModel.relativeId)
        delay(5000)
        Assert.assertEquals(Unit, result)
    }

    @Test
    fun deleteRelationTest() = runTest {
        `when`(relationRepository.deleteRelation(viewModel.patientId, viewModel.relativeId)).thenReturn(1)
        val result = viewModel.deleteRelation(viewModel.patientId, viewModel.relativeId)
        delay(5000)
        Assert.assertEquals(Unit,result)
    }

    @Test
    fun `discard relations`() = runTest{
        val result = viewModel.discardRelations()
        delay(5000)
        assertEquals(Unit,result)
    }

    @Test
    fun `update relations`() = runTest{
        val result = viewModel.updateRelation(relationEntity.toRelation())
        delay(5000)
        assertEquals(Unit,result)
    }

    @Test
    fun `add relation to genericEntity`() = runTest {
        viewModel.relationBetween = listOf(relationView)
        val result = viewModel.addRelationsToGenericEntity()
        delay(5000)
        assertEquals(Unit,result)
    }
}