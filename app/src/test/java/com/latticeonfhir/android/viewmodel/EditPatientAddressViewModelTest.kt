package com.latticeonfhir.android.viewmodel

import com.latticeonfhir.android.base.BaseClass
import com.latticeonfhir.android.data.local.enums.ChangeTypeEnum
import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.model.patch.ChangeRequest
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.patient.PatientRepository
import com.latticeonfhir.android.ui.patienteditscreen.address.EditPatientAddressViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class EditPatientAddressViewModelTest: BaseClass() {

    lateinit var viewModel: EditPatientAddressViewModel

    @Mock
    lateinit var patientRepository: PatientRepository

    @Mock
    lateinit var genericRepository: GenericRepository

    @OptIn(DelicateCoroutinesApi::class)
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
  public override fun setUp() {
        MockitoAnnotations.initMocks(this)
        viewModel = EditPatientAddressViewModel(
            patientRepository = patientRepository,
            genericRepository = genericRepository
        )

        Dispatchers.setMain(mainThreadSurrogate)
    }

    @Test
    fun valid_home_address_inputs() {
        viewModel.homeAddress.pincode = "999999"
        viewModel.homeAddress.addressLine1 = "Address Line 1"
        viewModel.homeAddress.city = "City"
        viewModel.homeAddress.state = "Uttarakhand"
        val result = viewModel.addressInfoValidation()
        assertEquals(true, result)
    }

    @Test
    fun invalid_pincode() {
        viewModel.homeAddress.pincode = "99999"
        viewModel.homeAddress.addressLine1 = "Address Line 1"
        viewModel.homeAddress.city = "City"
        viewModel.homeAddress.state = "Uttarakhand"
        val result = viewModel.addressInfoValidation()
        assertEquals(false, result)
    }

    @Test
    fun empty_state() {
        viewModel.homeAddress.pincode = "999999"
        viewModel.homeAddress.addressLine1 = "Address Line 1"
        viewModel.homeAddress.city = "City"
        viewModel.homeAddress.state = ""
        val result = viewModel.addressInfoValidation()
        assertEquals(false, result)
    }

    @Test
    fun empty_address_line_1() {
        viewModel.homeAddress.pincode = "999999"
        viewModel.homeAddress.addressLine1 = ""
        viewModel.homeAddress.city = "City"
        viewModel.homeAddress.state = "Uttarakhand"
        val result = viewModel.addressInfoValidation()
        assertEquals(false, result)
    }

    @Test
    fun empty_city() {
        viewModel.homeAddress.pincode = "999999"
        viewModel.homeAddress.addressLine1 = "Address Line 1"
        viewModel.homeAddress.city = ""
        viewModel.homeAddress.state = "Uttarakhand"
        val result = viewModel.addressInfoValidation()
        assertEquals(false, result)
    }

    @Test
    fun valid_work_address_inputs() {
        viewModel.homeAddress.pincode = "999999"
        viewModel.homeAddress.addressLine1 = "Address Line 1"
        viewModel.homeAddress.city = "City"
        viewModel.homeAddress.state = "Uttarakhand"
        viewModel.addWorkAddress = true
        viewModel.workAddress.pincode = "999999"
        viewModel.workAddress.addressLine1 = "Address Line 1"
        viewModel.workAddress.city = "City"
        viewModel.workAddress.state = "Uttarakhand"
        val result = viewModel.addressInfoValidation()
        assertEquals(true, result)
    }

    @Test
    fun empty_work_address_fields() {
        viewModel.homeAddress.pincode = "999999"
        viewModel.homeAddress.addressLine1 = "Address Line 1"
        viewModel.homeAddress.city = "City"
        viewModel.homeAddress.state = "Uttarakhand"
        viewModel.addWorkAddress = true
        val result = viewModel.addressInfoValidation()
        assertEquals(false, result)
    }

    // check is Edit any field
    @Test
    fun checkIfAnyFieldEditReturnTrue() {
        // set data in fields
        viewModel.homeAddress.pincode = "999999"
        viewModel.homeAddress.addressLine1 = "Address Line 1"
        viewModel.homeAddress.city = "City"
        viewModel.homeAddress.state = "Uttarakhand"

        // set data in tempField
        viewModel.homeAddressTemp.pincode = viewModel.homeAddress.pincode
        viewModel.homeAddressTemp.addressLine1 = viewModel.homeAddress.addressLine1
        viewModel.homeAddressTemp.city = viewModel.homeAddress.city
        viewModel.homeAddressTemp.state = viewModel.homeAddress.state


        // edit any field
        viewModel.homeAddress.pincode = ""

        val isEdit = viewModel.checkIsEdit()
        assertEquals(true, isEdit)
    }
    @Test
    fun checkForRevertChanges(){
        // set data in fields
        viewModel.homeAddress.pincode = "999999"
        viewModel.homeAddress.addressLine1 = "Address Line 1"
        viewModel.homeAddress.city = "City"
        viewModel.homeAddress.state = "Uttarakhand"

        // set data in tempField
        viewModel.homeAddressTemp.pincode = viewModel.homeAddress.pincode
        viewModel.homeAddressTemp.addressLine1 = viewModel.homeAddress.addressLine1
        viewModel.homeAddressTemp.city = viewModel.homeAddress.city
        viewModel.homeAddressTemp.state = viewModel.homeAddress.state

        val result= viewModel.revertChanges()
        assertEquals(true, result)


    }

    @Test
    fun checkIfFieldNotEditReturnFalse() {
        // set data in fields
        viewModel.homeAddress.pincode = "999999"
        viewModel.homeAddress.addressLine1 = "Address Line 1"
        viewModel.homeAddress.city = "City"
        viewModel.homeAddress.state = "Uttarakhand"

        // set data in tempField
        viewModel.homeAddressTemp.pincode = viewModel.homeAddress.pincode
        viewModel.homeAddressTemp.addressLine1 = viewModel.homeAddress.addressLine1
        viewModel.homeAddressTemp.city = viewModel.homeAddress.city
        viewModel.homeAddressTemp.state = viewModel.homeAddress.state

        val isEdit = viewModel.checkIsEdit()
        assertEquals(false, isEdit)
    }

    @Test
    fun checkUpdateBasicInfo(): Unit = runBlocking {
        Mockito.`when`(patientRepository.updatePatientData(patientResponse)).thenReturn(1)
        viewModel.homeAddress.pincode = "999999"
        viewModel.homeAddress.addressLine1 = "Address Line 1"
        viewModel.homeAddress.city = "City"
        viewModel.homeAddress.state = "Uttarakhand"
        viewModel.homeAddress.addressLine2 = "Uttarakhand"
        viewModel.homeAddress.district = "Uttarakhand"

        // set data in tempField
        viewModel.homeAddressTemp.pincode = "111111"
        viewModel.homeAddressTemp.addressLine1 = "ABC"
        viewModel.homeAddressTemp.city = "erwe"
        viewModel.homeAddressTemp.state = "Uttar Pradesh"
        viewModel.homeAddressTemp.addressLine2 = "fd"
        viewModel.homeAddressTemp.district = "fsldf"

        launch(Dispatchers.Main) {
            viewModel.updateBasicInfo(patientResponse = patientResponse)
        }
    }
    @Test
    fun checkUpdateBasicInfoPinCodeIsEmpty(): Unit = runBlocking {
        Mockito.`when`(patientRepository.updatePatientData(patientResponse)).thenReturn(1)
        viewModel.homeAddress.pincode = ""
        viewModel.homeAddress.addressLine1 = "Address Line 1"
        viewModel.homeAddress.city = "City"
        viewModel.homeAddress.state = "Uttarakhand"
        viewModel.homeAddress.addressLine2 = "Uttarakhand"
        viewModel.homeAddress.district = "Uttarakhand"

        // set data in tempField
        viewModel.homeAddressTemp.pincode = "111111"
        viewModel.homeAddressTemp.addressLine1 = "ABC"
        viewModel.homeAddressTemp.city = "erwe"
        viewModel.homeAddressTemp.state = "Uttar Pradesh"
        viewModel.homeAddressTemp.addressLine2 = "fd"
        viewModel.homeAddressTemp.district = "fsldf"

        launch(Dispatchers.Main) {
            viewModel.updateBasicInfo(patientResponse = patientResponse)
        }
    }
    @Test
    fun checkUpdateBasicInfoPinCodeNotIsEmpty(): Unit = runBlocking {
        Mockito.`when`(patientRepository.updatePatientData(patientResponse)).thenReturn(1)
        viewModel.homeAddress.pincode = "4534434"
        viewModel.homeAddress.addressLine1 = "Address Line 1"
        viewModel.homeAddress.city = "City"
        viewModel.homeAddress.state = "Uttarakhand"
        viewModel.homeAddress.addressLine2 = "Uttarakhand"
        viewModel.homeAddress.district = "Uttarakhand"

        // set data in tempField
        viewModel.homeAddressTemp.pincode = ""
        viewModel.homeAddressTemp.addressLine1 = "ABC"
        viewModel.homeAddressTemp.city = "erwe"
        viewModel.homeAddressTemp.state = "Uttar Pradesh"
        viewModel.homeAddressTemp.addressLine2 = "fd"
        viewModel.homeAddressTemp.district = "fsldf"

        launch(Dispatchers.Main) {
            viewModel.updateBasicInfo(patientResponse = patientResponse)
        }
    }
    @Test
    fun checkUpdateBasicInfoPinCodeTempNotIsEmpty(): Unit = runBlocking {
        Mockito.`when`(patientRepository.updatePatientData(patientResponse)).thenReturn(1)
        viewModel.homeAddress.pincode = "4534434"
        viewModel.homeAddress.addressLine1 = "Address Line 1"
        viewModel.homeAddress.city = "City"
        viewModel.homeAddress.state = "Uttarakhand"
        viewModel.homeAddress.addressLine2 = "Uttarakhand"
        viewModel.homeAddress.district = "Uttarakhand"

        // set data in tempField
        viewModel.homeAddressTemp.pincode = "345345"
        viewModel.homeAddressTemp.addressLine1 = "ABC"
        viewModel.homeAddressTemp.city = "erwe"
        viewModel.homeAddressTemp.state = "Uttar Pradesh"
        viewModel.homeAddressTemp.addressLine2 = "fd"
        viewModel.homeAddressTemp.district = "fsldf"

        launch(Dispatchers.Main) {
            viewModel.updateBasicInfo(patientResponse = patientResponse)
        }
    }
    @Test
    fun checkUpdateBasicInfoAddressLine1IsEmpty(): Unit = runBlocking {
        Mockito.`when`(patientRepository.updatePatientData(patientResponse)).thenReturn(1)
        viewModel.homeAddress.pincode = "4534434"
        viewModel.homeAddress.addressLine1 = ""
        viewModel.homeAddress.city = "City"
        viewModel.homeAddress.state = "Uttarakhand"
        viewModel.homeAddress.addressLine2 = "Uttarakhand"
        viewModel.homeAddress.district = "Uttarakhand"

        // set data in tempField
        viewModel.homeAddressTemp.pincode = "345345"
        viewModel.homeAddressTemp.addressLine1 = "ABC"
        viewModel.homeAddressTemp.city = "erwe"
        viewModel.homeAddressTemp.state = "Uttar Pradesh"
        viewModel.homeAddressTemp.addressLine2 = "fd"
        viewModel.homeAddressTemp.district = "fsldf"

        launch(Dispatchers.Main) {
            viewModel.updateBasicInfo(patientResponse = patientResponse)
        }
    }
    @Test
    fun checkUpdateBasicInfoAddressLine1NotIsEmpty(): Unit = runBlocking {
        Mockito.`when`(patientRepository.updatePatientData(patientResponse)).thenReturn(1)
        viewModel.homeAddress.pincode = "4534434"
        viewModel.homeAddress.addressLine1 = "443534"
        viewModel.homeAddress.city = "City"
        viewModel.homeAddress.state = "Uttarakhand"
        viewModel.homeAddress.addressLine2 = "Uttarakhand"
        viewModel.homeAddress.district = "Uttarakhand"

        // set data in tempField
        viewModel.homeAddressTemp.pincode = "345345"
        viewModel.homeAddressTemp.addressLine1 = ""
        viewModel.homeAddressTemp.city = "erwe"
        viewModel.homeAddressTemp.state = "Uttar Pradesh"
        viewModel.homeAddressTemp.addressLine2 = "fd"
        viewModel.homeAddressTemp.district = "fsldf"

        launch(Dispatchers.Main) {
            viewModel.updateBasicInfo(patientResponse = patientResponse)
        }
    }
    @Test
    fun checkUpdateBasicInfoAddressLine1TempNotIsEmpty(): Unit = runBlocking {
        Mockito.`when`(patientRepository.updatePatientData(patientResponse)).thenReturn(1)
        viewModel.homeAddress.pincode = "4534434"
        viewModel.homeAddress.addressLine1 = ""
        viewModel.homeAddress.city = "City"
        viewModel.homeAddress.state = "Uttarakhand"
        viewModel.homeAddress.addressLine2 = "Uttarakhand"
        viewModel.homeAddress.district = "Uttarakhand"

        // set data in tempField
        viewModel.homeAddressTemp.pincode = "345345"
        viewModel.homeAddressTemp.addressLine1 = "rewr"
        viewModel.homeAddressTemp.city = "erwe"
        viewModel.homeAddressTemp.state = "Uttar Pradesh"
        viewModel.homeAddressTemp.addressLine2 = "fd"
        viewModel.homeAddressTemp.district = "fsldf"

        launch(Dispatchers.Main) {
            viewModel.updateBasicInfo(patientResponse = patientResponse)
        }
    }
    @Test
    fun checkUpdateBasicInfoAddressLine2IsEmpty(): Unit = runBlocking {
        Mockito.`when`(patientRepository.updatePatientData(patientResponse)).thenReturn(1)
        viewModel.homeAddress.pincode = "4534434"
        viewModel.homeAddress.addressLine1 = "dsf"
        viewModel.homeAddress.city = "City"
        viewModel.homeAddress.state = "Uttarakhand"
        viewModel.homeAddress.addressLine2 = ""
        viewModel.homeAddress.district = "Uttarakhand"

        // set data in tempField
        viewModel.homeAddressTemp.pincode = "345345"
        viewModel.homeAddressTemp.addressLine1 = "ABC"
        viewModel.homeAddressTemp.city = "erwe"
        viewModel.homeAddressTemp.state = "Uttar Pradesh"
        viewModel.homeAddressTemp.addressLine2 = "fd"
        viewModel.homeAddressTemp.district = "fsldf"

        launch(Dispatchers.Main) {
            viewModel.updateBasicInfo(patientResponse = patientResponse)
        }
    }
    @Test
    fun checkUpdateBasicInfoAddressLine2NotIsEmpty(): Unit = runBlocking {
        Mockito.`when`(patientRepository.updatePatientData(patientResponse)).thenReturn(1)
        viewModel.homeAddress.pincode = "4534434"
        viewModel.homeAddress.addressLine1 = "dfg"
        viewModel.homeAddress.city = "City"
        viewModel.homeAddress.state = "Uttarakhand"
        viewModel.homeAddress.addressLine2 = "Uttarakhand"
        viewModel.homeAddress.district = "Uttarakhand"

        // set data in tempField
        viewModel.homeAddressTemp.pincode = "345345"
        viewModel.homeAddressTemp.addressLine1 = ""
        viewModel.homeAddressTemp.city = "erwe"
        viewModel.homeAddressTemp.state = "Uttar Pradesh"
        viewModel.homeAddressTemp.addressLine2 = "fd"
        viewModel.homeAddressTemp.district = "fsldf"

        launch(Dispatchers.Main) {
            viewModel.updateBasicInfo(patientResponse = patientResponse)
        }
    }
    @Test
    fun checkUpdateBasicInfoAddressLine2TempNotIsEmpty(): Unit = runBlocking {
        Mockito.`when`(patientRepository.updatePatientData(patientResponse)).thenReturn(1)
        viewModel.homeAddress.pincode = "4534434"
        viewModel.homeAddress.addressLine1 = ""
        viewModel.homeAddress.city = "City"
        viewModel.homeAddress.state = "Uttarakhand"
        viewModel.homeAddress.addressLine2 = "Uttarakhand"
        viewModel.homeAddress.district = "Uttarakhand"

        // set data in tempField
        viewModel.homeAddressTemp.pincode = "345345"
        viewModel.homeAddressTemp.addressLine1 = "rewr"
        viewModel.homeAddressTemp.city = "erwe"
        viewModel.homeAddressTemp.state = "Uttar Pradesh"
        viewModel.homeAddressTemp.addressLine2 = "fd"
        viewModel.homeAddressTemp.district = "fsldf"

        launch(Dispatchers.Main) {
            viewModel.updateBasicInfo(patientResponse = patientResponse)
        }
    }
    @Test
    fun checkUpdateBasicInfoStateIsEmpty(): Unit = runBlocking {
        Mockito.`when`(patientRepository.updatePatientData(patientResponse)).thenReturn(1)
        viewModel.homeAddress.pincode = "4534434"
        viewModel.homeAddress.addressLine1 = "dsf"
        viewModel.homeAddress.city = "City"
        viewModel.homeAddress.state = ""
        viewModel.homeAddress.addressLine2 = ""
        viewModel.homeAddress.district = "Uttarakhand"

        // set data in tempField
        viewModel.homeAddressTemp.pincode = "345345"
        viewModel.homeAddressTemp.addressLine1 = "ABC"
        viewModel.homeAddressTemp.city = "erwe"
        viewModel.homeAddressTemp.state = "Uttar Pradesh"
        viewModel.homeAddressTemp.addressLine2 = "fd"
        viewModel.homeAddressTemp.district = "fsldf"

        launch(Dispatchers.Main) {
            viewModel.updateBasicInfo(patientResponse = patientResponse)
        }
    }
    @Test
    fun checkUpdateBasicInfoStateNotIsEmpty(): Unit = runBlocking {
        Mockito.`when`(patientRepository.updatePatientData(patientResponse)).thenReturn(1)
        viewModel.homeAddress.pincode = "4534434"
        viewModel.homeAddress.addressLine1 = "dfg"
        viewModel.homeAddress.city = "City"
        viewModel.homeAddress.state = "Uttarakhand"
        viewModel.homeAddress.addressLine2 = "Uttarakhand"
        viewModel.homeAddress.district = "Uttarakhand"

        // set data in tempField
        viewModel.homeAddressTemp.pincode = "345345"
        viewModel.homeAddressTemp.addressLine1 = ""
        viewModel.homeAddressTemp.city = "erwe"
        viewModel.homeAddressTemp.state = ""
        viewModel.homeAddressTemp.addressLine2 = "fd"
        viewModel.homeAddressTemp.district = "fsldf"

        launch(Dispatchers.Main) {
            viewModel.updateBasicInfo(patientResponse = patientResponse)
        }
    }
    @Test
    fun checkUpdateBasicInfoStateTempTempNotIsEmpty(): Unit = runBlocking {
        Mockito.`when`(patientRepository.updatePatientData(patientResponse)).thenReturn(1)
        viewModel.homeAddress.pincode = "4534434"
        viewModel.homeAddress.addressLine1 = ""
        viewModel.homeAddress.city = "City"
        viewModel.homeAddress.state = "Uttarakhand"
        viewModel.homeAddress.addressLine2 = "Uttarakhand"
        viewModel.homeAddress.district = "Uttarakhand"

        // set data in tempField
        viewModel.homeAddressTemp.pincode = "345345"
        viewModel.homeAddressTemp.addressLine1 = "rewr"
        viewModel.homeAddressTemp.city = "erwe"
        viewModel.homeAddressTemp.state = "Uttar Pradesh"
        viewModel.homeAddressTemp.addressLine2 = "fd"
        viewModel.homeAddressTemp.district = "fsldf"

        launch(Dispatchers.Main) {
            viewModel.updateBasicInfo(patientResponse = patientResponse)
        }
    }
    @Test
    fun checkUpdateBasicInfoFhirIdNull(): Unit = runBlocking {
        Mockito.`when`(patientRepository.updatePatientData(patientResponse)).thenReturn(1)
        viewModel.homeAddress.pincode = "4534434"
        viewModel.homeAddress.addressLine1 = ""
        viewModel.homeAddress.city = "City"
        viewModel.homeAddress.state = "Uttarakhand"
        viewModel.homeAddress.addressLine2 = "Uttarakhand"
        viewModel.homeAddress.district = "Uttarakhand"

        // set data in tempField
        viewModel.homeAddressTemp.pincode = "345345"
        viewModel.homeAddressTemp.addressLine1 = "rewr"
        viewModel.homeAddressTemp.city = "erwe"
        viewModel.homeAddressTemp.state = "Uttar Pradesh"
        viewModel.homeAddressTemp.addressLine2 = "fd"
        viewModel.homeAddressTemp.district = "fsldf"
        patientResponse.copy(fhirId = null)

        launch(Dispatchers.Main) {
            viewModel.updateBasicInfo(patientResponse = patientResponse)
        }
    }

}