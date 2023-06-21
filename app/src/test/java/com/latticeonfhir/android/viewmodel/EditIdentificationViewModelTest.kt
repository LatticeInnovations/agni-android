package com.latticeonfhir.android.viewmodel

import com.latticeonfhir.android.base.BaseClass
import com.latticeonfhir.android.data.local.enums.ChangeTypeEnum
import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.model.patch.ChangeRequest
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.identifier.IdentifierRepository
import com.latticeonfhir.android.data.local.repository.patient.PatientRepository
import com.latticeonfhir.android.ui.patienteditscreen.identification.EditIdentificationViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class EditIdentificationViewModelTest : BaseClass() {

    lateinit var viewModel: EditIdentificationViewModel

    @Mock
    lateinit var patientRepository: PatientRepository

    @Mock
    lateinit var genericRepository: GenericRepository

    @Mock
    lateinit var identifierRepository: IdentifierRepository

    @OptIn(DelicateCoroutinesApi::class)
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @Before
    public override fun setUp() {
        MockitoAnnotations.initMocks(this)
        viewModel = EditIdentificationViewModel(
            patientRepository = patientRepository,
            genericRepository = genericRepository,
            identifierRepository = identifierRepository
        )
        Dispatchers.setMain(mainThreadSurrogate)


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

    @Test
    fun checkRevertChanges() {
        viewModel.isPassportSelected = true
        viewModel.isVoterSelected = true
        viewModel.isPatientSelected = true
        viewModel.passportId = "A1098765"
        viewModel.voterId = "XYZ1234567"
        viewModel.patientId = "ABCDE12345"

        // set data in tempField
        viewModel.isPassportSelectedTemp = false
        viewModel.isVoterSelectedTemp = false
        viewModel.isPatientSelectedTemp = false
        viewModel.passportIdTemp = ""
        viewModel.voterIdTemp = ""
        viewModel.patientIdTemp = ""
        val isChanged = viewModel.revertChanges()
        assertEquals(true, isChanged)
    }

    @Test
    fun checkUpdateBasicInfo(): Unit = runBlocking {
        viewModel.isPassportSelected = true
        viewModel.isVoterSelected = true
        viewModel.isPatientSelected = true
        viewModel.passportId = "A1098765"
        viewModel.voterId = "XYZ1234567"
        viewModel.patientId = "ABCDE12345"

        // set data in tempField
        viewModel.isPassportSelectedTemp = false
        viewModel.isVoterSelectedTemp = false
        viewModel.isPatientSelectedTemp = false
        viewModel.passportIdTemp = ""
        viewModel.voterIdTemp = ""
        viewModel.patientIdTemp = ""
        Mockito.`when`(patientRepository.updatePatientData(patientResponse)).thenReturn(1)

        launch(Dispatchers.Main) {
            viewModel.updateBasicInfo(patientResponse = patientResponse)
        }
    }
    @Test
    fun checkUpdateBasicInfoIsPassportIdEmpty(): Unit = runTest {
        viewModel.isPassportSelected = true
        viewModel.isVoterSelected = true
        viewModel.isPatientSelected = true
        viewModel.passportId = ""
        viewModel.voterId = "XYZ1234567"
        viewModel.patientId = "ABCDE12345"

        // set data in tempField
        viewModel.isPassportSelectedTemp = false
        viewModel.isVoterSelectedTemp = false
        viewModel.isPatientSelectedTemp = false
        viewModel.passportIdTemp = ""
        viewModel.voterIdTemp = ""
        viewModel.patientIdTemp = "47394453"
        Mockito.`when`(patientRepository.updatePatientData(patientResponse)).thenReturn(1)

        launch(Dispatchers.Main) {
            viewModel.updateBasicInfo(patientResponse = patientResponse)
        }
    }
    @Test
    fun checkUpdateBasicInfoIsPassportIdNotEmpty(): Unit = runTest {
        viewModel.isPassportSelected = true
        viewModel.isVoterSelected = true
        viewModel.isPatientSelected = true
        viewModel.passportId = "345sdfsd"
        viewModel.voterId = "XYZ1234567"
        viewModel.patientId = "ABCDE12345"

        // set data in tempField
        viewModel.isPassportSelectedTemp = false
        viewModel.isVoterSelectedTemp = false
        viewModel.isPatientSelectedTemp = false
        viewModel.passportIdTemp = ""
        viewModel.voterIdTemp = ""
        viewModel.patientIdTemp = "47394453"
        Mockito.`when`(patientRepository.updatePatientData(patientResponse)).thenReturn(1)

        launch(Dispatchers.Main) {
            viewModel.updateBasicInfo(patientResponse = patientResponse)
        }
    }
    @Test
    fun checkUpdateBasicInfoIsVoterIdEmpty(): Unit = runTest {
        viewModel.isPassportSelected = true
        viewModel.isVoterSelected = true
        viewModel.isPatientSelected = true
        viewModel.passportId = "345sdfsd"
        viewModel.voterId = ""
        viewModel.patientId = "ABCDE12345"

        // set data in tempField
        viewModel.isPassportSelectedTemp = false
        viewModel.isVoterSelectedTemp = false
        viewModel.isPatientSelectedTemp = false
        viewModel.passportIdTemp = ""
        viewModel.voterIdTemp = "XYZ1234567"
        viewModel.patientIdTemp = "47394453"
        Mockito.`when`(patientRepository.updatePatientData(patientResponse)).thenReturn(1)

        launch(Dispatchers.Main) {
            viewModel.updateBasicInfo(patientResponse = patientResponse)
        }
    }
    @Test
    fun checkUpdateBasicInfoIsVoterIdNotEmpty(): Unit = runTest {
        viewModel.isPassportSelected = true
        viewModel.isVoterSelected = true
        viewModel.isPatientSelected = true
        viewModel.passportId = "345sdfsd"
        viewModel.voterId = "AAA11111"
        viewModel.patientId = "ABCDE12345"

        // set data in tempField
        viewModel.isPassportSelectedTemp = false
        viewModel.isVoterSelectedTemp = false
        viewModel.isPatientSelectedTemp = false
        viewModel.passportIdTemp = ""
        viewModel.voterIdTemp = "XYZ1234567"
        viewModel.patientIdTemp = "47394453"
        Mockito.`when`(patientRepository.updatePatientData(patientResponse)).thenReturn(1)

        launch(Dispatchers.Main) {
            viewModel.updateBasicInfo(patientResponse = patientResponse)
        }
    }
    @Test
    fun checkUpdateBasicInfoIsPatientIdEmpty(): Unit = runTest {
        viewModel.isPassportSelected = true
        viewModel.isVoterSelected = true
        viewModel.isPatientSelected = true
        viewModel.passportId = "345sdfsd"
        viewModel.voterId = "4354"
        viewModel.patientId = ""

        // set data in tempField
        viewModel.isPassportSelectedTemp = false
        viewModel.isVoterSelectedTemp = false
        viewModel.isPatientSelectedTemp = false
        viewModel.passportIdTemp = ""
        viewModel.voterIdTemp = "XYZ1234567"
        viewModel.patientIdTemp = "47394453"
        Mockito.`when`(patientRepository.updatePatientData(patientResponse)).thenReturn(1)

        launch(Dispatchers.Main) {
            viewModel.updateBasicInfo(patientResponse = patientResponse)
        }
    }
    @Test
    fun checkUpdateBasicInfoIsPatientTemIdEmpty(): Unit = runTest {
        viewModel.isPassportSelected = true
        viewModel.isVoterSelected = true
        viewModel.isPatientSelected = true
        viewModel.passportId = "345sdfsd"
        viewModel.voterId = "4354"
        viewModel.patientId = "3423fdsf"

        // set data in tempField
        viewModel.isPassportSelectedTemp = false
        viewModel.isVoterSelectedTemp = false
        viewModel.isPatientSelectedTemp = false
        viewModel.passportIdTemp = ""
        viewModel.voterIdTemp = "XYZ1234567"
        viewModel.patientIdTemp = ""
        Mockito.`when`(patientRepository.updatePatientData(patientResponse)).thenReturn(1)

        launch(Dispatchers.Main) {
            viewModel.updateBasicInfo(patientResponse = patientResponse)
        }
    }
    @Test
    fun checkUpdateBasicInfoIsPatientIdNotEmpty(): Unit = runTest {
        viewModel.isPassportSelected = true
        viewModel.isVoterSelected = true
        viewModel.isPatientSelected = true
        viewModel.passportId = "345sdfsd"
        viewModel.voterId = "AAA11111"
        viewModel.patientId = "ABCDE12345"

        // set data in tempField
        viewModel.isPassportSelectedTemp = false
        viewModel.isVoterSelectedTemp = false
        viewModel.isPatientSelectedTemp = false
        viewModel.passportIdTemp = ""
        viewModel.voterIdTemp = "XYZ1234567"
        viewModel.patientIdTemp = "47394453"
        Mockito.`when`(patientRepository.updatePatientData(patientResponse)).thenReturn(1)

        launch(Dispatchers.Main) {
            viewModel.updateBasicInfo(patientResponse = patientResponse)
        }
    } @Test
    fun checkUpdateBasicInfoIsPatientIdTemNotEmpty(): Unit = runTest {
        viewModel.isPassportSelected = true
        viewModel.isVoterSelected = true
        viewModel.isPatientSelected = true
        viewModel.passportId = "345sdfsd"
        viewModel.voterId = "AAA11111"
        viewModel.patientId = "ABCDE12345"

        // set data in tempField
        viewModel.isPassportSelectedTemp = false
        viewModel.isVoterSelectedTemp = false
        viewModel.isPatientSelectedTemp = false
        viewModel.passportIdTemp = ""
        viewModel.voterIdTemp = "XYZ1234567"
        viewModel.patientIdTemp = "47394453"
        Mockito.`when`(patientRepository.updatePatientData(patientResponse)).thenReturn(1)

        launch(Dispatchers.Main) {
            viewModel.updateBasicInfo(patientResponse = patientResponse)
        }
    }
    @Test
    fun checkUpdateBasicInfoIsFhirIdNull(): Unit = runTest {
        viewModel.isPassportSelected = true
        viewModel.isVoterSelected = true
        viewModel.isPatientSelected = true
        viewModel.passportId = "345sdfsd"
        viewModel.voterId = "AAA11111"
        viewModel.patientId = "ABCDE12345"

        // set data in tempField
        viewModel.isPassportSelectedTemp = false
        viewModel.isVoterSelectedTemp = false
        viewModel.isPatientSelectedTemp = false
        viewModel.passportIdTemp = ""
        viewModel.voterIdTemp = "XYZ1234567"
        viewModel.patientIdTemp = "47394453"
        patientResponse.copy(fhirId = null)
        Mockito.`when`(patientRepository.updatePatientData(patientResponse)).thenReturn(1)

        launch(Dispatchers.Main) {
            viewModel.updateBasicInfo(patientResponse = patientResponse)
        }
    }


}