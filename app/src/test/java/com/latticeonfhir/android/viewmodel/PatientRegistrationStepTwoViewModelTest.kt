package com.latticeonfhir.android.viewmodel

import com.latticeonfhir.android.ui.patientregistration.step2.PatientRegistrationStepTwoViewModel
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class PatientRegistrationStepTwoViewModelTest {

    lateinit var viewModel: PatientRegistrationStepTwoViewModel

    @Before
    fun setUp() {
        viewModel = PatientRegistrationStepTwoViewModel()
    }

    @Test
    fun valid_identity_inputs_all_fields(){
        viewModel.isPassportSelected = true
        viewModel.isVoterSelected = true
        viewModel.isPatientSelected = true
        viewModel.passportId = "A1098765"
        viewModel.voterId = "XYZ1234567"
        viewModel.patientId = "ABCDE12345"
        val result = viewModel.identityInfoValidation()
        Assert.assertEquals(true, result)
    }

    @Test
    fun unselect_all_fields(){
        viewModel.isPassportSelected = false
        viewModel.isVoterSelected = false
        viewModel.isPatientSelected = false
        val result = viewModel.identityInfoValidation()
        Assert.assertEquals(false, result)
    }

    @Test
    fun invalid_passport_id(){
        viewModel.isPassportSelected = true
        viewModel.isVoterSelected = false
        viewModel.isPatientSelected = false
        viewModel.passportId = "BB12345"
        val result = viewModel.identityInfoValidation()
        Assert.assertEquals(false, result)
    }

    @Test
    fun invalid_voter_id(){
        viewModel.isPassportSelected = false
        viewModel.isVoterSelected = true
        viewModel.isPatientSelected = false
        viewModel.voterId = "123XYZ1234"
        val result = viewModel.identityInfoValidation()
        Assert.assertEquals(false, result)
    }

    @Test
    fun invalid_patient_id(){
        viewModel.isPassportSelected = false
        viewModel.isVoterSelected = false
        viewModel.isPatientSelected = true
        viewModel.patientId = "1234"
        val result = viewModel.identityInfoValidation()
        Assert.assertEquals(false, result)
    }
}