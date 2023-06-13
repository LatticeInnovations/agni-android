package com.latticeonfhir.android.viewmodel

import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.patient.PatientRepository
import com.latticeonfhir.android.ui.patienteditscreen.address.EditPatientAddressViewModel
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class EditPatientAddressViewModelTest {

    lateinit var viewModel: EditPatientAddressViewModel

    @Mock
    lateinit var patientRepository: PatientRepository

    @Mock
    lateinit var genericRepository: GenericRepository

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        viewModel = EditPatientAddressViewModel(
            patientRepository = patientRepository,
            genericRepository = genericRepository
        )
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
}