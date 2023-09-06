package com.latticeonfhir.android.viewmodel.editpatient

import com.latticeonfhir.android.base.BaseClass
import com.latticeonfhir.android.data.local.repository.patient.PatientRepository
import com.latticeonfhir.android.ui.patientprofile.PatientProfileViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class PatientProfileViewModelTest: BaseClass() {

    @Mock
    private lateinit var patientRepository: PatientRepository

    private lateinit var patientProfileViewModel: PatientProfileViewModel

    @Before
    public override fun setUp() {
        MockitoAnnotations.openMocks(this)
        patientProfileViewModel = PatientProfileViewModel(patientRepository)
    }

    @Test
    fun `get patient data by id`() = runTest {
        `when`(patientRepository.getPatientById(patientResponse.id)).thenReturn(
            listOf(
                patientResponse
            )
        )
        val result = patientProfileViewModel.getPatientData(patientResponse.id)
        assertEquals(patientResponse, result)
    }
}