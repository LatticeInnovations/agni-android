package com.latticeonfhir.android.viewmodel

import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.identifier.IdentifierRepository
import com.latticeonfhir.android.data.local.repository.patient.PatientRepository
import com.latticeonfhir.android.ui.patienteditscreen.identification.EditIdentificationViewModel
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class EditIdentificationViewModelTest {

    lateinit var viewModel: EditIdentificationViewModel

    @Mock
    lateinit var patientRepository: PatientRepository

    @Mock
    lateinit var genericRepository: GenericRepository

    @Mock
    lateinit var identifierRepository: IdentifierRepository

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        viewModel = EditIdentificationViewModel(
            patientRepository = patientRepository,
            genericRepository = genericRepository,
            identifierRepository = identifierRepository
        )
    }

    @Test
    fun valid_identity_inputs_all_fields() {
        viewModel.isPassportSelected = true
        viewModel.isVoterSelected = true
        viewModel.isPatientSelected = true
        viewModel.passportId = "A1098765"
        viewModel.voterId = "XYZ1234567"
        viewModel.patientId = "ABCDE12345"
        val result = viewModel.identityInfoValidation()
        assertEquals(true, result)
    }

    @Test
    fun unselect_all_fields() {
        viewModel.isPassportSelected = false
        viewModel.isVoterSelected = false
        viewModel.isPatientSelected = false
        val result = viewModel.identityInfoValidation()
        assertEquals(false, result)
    }

    @Test
    fun invalid_passport_id() {
        viewModel.isPassportSelected = true
        viewModel.isVoterSelected = false
        viewModel.isPatientSelected = false
        viewModel.passportId = "BB12345"
        val result = viewModel.identityInfoValidation()
        assertEquals(false, result)
    }

    @Test
    fun invalid_voter_id() {
        viewModel.isPassportSelected = false
        viewModel.isVoterSelected = true
        viewModel.isPatientSelected = false
        viewModel.voterId = "123XYZ1234"
        val result = viewModel.identityInfoValidation()
        assertEquals(false, result)
    }

    @Test
    fun invalid_patient_id() {
        viewModel.isPassportSelected = false
        viewModel.isVoterSelected = false
        viewModel.isPatientSelected = true
        viewModel.patientId = "1234"
        val result = viewModel.identityInfoValidation()
        assertEquals(false, result)
    }

    // check is Edit any field
    @Test
    fun checkIfAnyFieldEditReturnTrue() {
        // set data in fields
        viewModel.isPassportSelected = true
        viewModel.isVoterSelected = true
        viewModel.isPatientSelected = true
        viewModel.passportId = "A1098765"
        viewModel.voterId = "XYZ1234567"
        viewModel.patientId = "ABCDE12345"

        // set data in tempField
        viewModel.isPassportSelectedTemp = viewModel.isPassportSelected
        viewModel.isVoterSelectedTemp = viewModel.isVoterSelected
        viewModel.isPatientSelectedTemp = viewModel.isPatientSelected
        viewModel.passportIdTemp = viewModel.passportId
        viewModel.voterIdTemp = viewModel.voterId
        viewModel.patientIdTemp = viewModel.patientId


        // edit any field
        viewModel.isPatientSelected = false
        viewModel.patientId = ""

        val isEdit = viewModel.checkIsEdit()
        assertEquals(true, isEdit)
    }

    @Test
    fun checkIfFieldNotEditReturnFalse() {
        // set data in fields
        viewModel.isPassportSelected = true
        viewModel.isVoterSelected = true
        viewModel.isPatientSelected = true
        viewModel.passportId = "A1098765"
        viewModel.voterId = "XYZ1234567"
        viewModel.patientId = "ABCDE12345"

        // set data in tempField
        viewModel.isPassportSelectedTemp = viewModel.isPassportSelected
        viewModel.isVoterSelectedTemp = viewModel.isVoterSelected
        viewModel.isPatientSelectedTemp = viewModel.isPatientSelected
        viewModel.passportIdTemp = viewModel.passportId
        viewModel.voterIdTemp = viewModel.voterId
        viewModel.patientIdTemp = viewModel.patientId
        val isEdit = viewModel.checkIsEdit()
        assertEquals(false, isEdit)
    }


}