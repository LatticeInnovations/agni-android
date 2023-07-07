package com.latticeonfhir.android.viewmodel

import com.latticeonfhir.android.data.local.repository.patient.PatientRepository
import com.latticeonfhir.android.data.local.repository.relation.RelationRepository
import com.latticeonfhir.android.base.BaseClass
import com.latticeonfhir.android.ui.householdmember.members.MembersScreenViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class MembersScreenViewModelTest : BaseClass() {
    @Mock
    lateinit var patientRepository: PatientRepository

    @Mock
    lateinit var relationRepository: RelationRepository
    lateinit var viewModel: MembersScreenViewModel

    @Before
    public override fun setUp() {
        MockitoAnnotations.openMocks(this)
        viewModel = MembersScreenViewModel(relationRepository, patientRepository)
    }

    @Test
    fun getAllRelationsTest() = runBlocking {
        `when`(relationRepository.getAllRelationOfPatient(id)).thenReturn(listOf(relationEntity))
        `when`(patientRepository.getPatientById(relativeId)).thenReturn(listOf(relative))

        viewModel.getAllRelations(id)
        delay(2000)
        Assert.assertEquals(listOf(relationEntity), viewModel.relationsList)
    }

    @Test
    fun getPatientDataTest() = runBlocking {
        `when`(patientRepository.getPatientById(id)).thenReturn(listOf(patientResponse))

        Assert.assertEquals(patientResponse, viewModel.getPatientData(id))
    }
}