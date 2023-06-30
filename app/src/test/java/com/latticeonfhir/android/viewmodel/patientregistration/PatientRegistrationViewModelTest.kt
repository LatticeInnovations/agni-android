package com.latticeonfhir.android.viewmodel.patientregistration

import com.latticeonfhir.android.base.BaseClass
import com.latticeonfhir.android.ui.patientregistration.PatientRegistrationViewModel
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class PatientRegistrationViewModelTest: BaseClass() {

    private lateinit var patientRegistrationViewModel: PatientRegistrationViewModel

    @Before
    public override fun setUp() {
        patientRegistrationViewModel = PatientRegistrationViewModel()
    }

    @Test
    fun `test patient registration view model`() {
        assertEquals(false,patientRegistrationViewModel.isLaunched)
    }

}