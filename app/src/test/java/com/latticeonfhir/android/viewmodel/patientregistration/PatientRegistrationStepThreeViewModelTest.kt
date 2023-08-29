package com.latticeonfhir.android.viewmodel.patientregistration

import com.latticeonfhir.android.ui.patientregistration.step3.PatientRegistrationStepThreeViewModel
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class PatientRegistrationStepThreeViewModelTest {
    lateinit var viewModel: PatientRegistrationStepThreeViewModel

    @Before
    fun setUp(){
        viewModel = PatientRegistrationStepThreeViewModel()
    }

    @Test
    fun valid_home_address_inputs() {
        viewModel.homeAddress.pincode = "999999"
        viewModel.homeAddress.addressLine1 = "Address Line 1"
        viewModel.homeAddress.city = "City"
        viewModel.homeAddress.state = "Uttarakhand"
        val result = viewModel.addressInfoValidation()
        Assert.assertEquals(true, result)
    }

    @Test
    fun invalid_pincode() {
        viewModel.homeAddress.pincode = "99999"
        viewModel.homeAddress.addressLine1 = "Address Line 1"
        viewModel.homeAddress.city = "City"
        viewModel.homeAddress.state = "Uttarakhand"
        val result = viewModel.addressInfoValidation()
        Assert.assertEquals(false, result)
    }

    @Test
    fun empty_state() {
        viewModel.homeAddress.pincode = "999999"
        viewModel.homeAddress.addressLine1 = "Address Line 1"
        viewModel.homeAddress.city = "City"
        viewModel.homeAddress.state = ""
        val result = viewModel.addressInfoValidation()
        Assert.assertEquals(false, result)
    }

    @Test
    fun empty_address_line_1() {
        viewModel.homeAddress.pincode = "999999"
        viewModel.homeAddress.addressLine1 = ""
        viewModel.homeAddress.city = "City"
        viewModel.homeAddress.state = "Uttarakhand"
        val result = viewModel.addressInfoValidation()
        Assert.assertEquals(false, result)
    }

    @Test
    fun empty_city() {
        viewModel.homeAddress.pincode = "999999"
        viewModel.homeAddress.addressLine1 = "Address Line 1"
        viewModel.homeAddress.city = ""
        viewModel.homeAddress.state = "Uttarakhand"
        val result = viewModel.addressInfoValidation()
        Assert.assertEquals(false, result)
    }

    @Test
    fun valid_work_address_inputs(){
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
        Assert.assertEquals(true, result)
    }

    @Test
    fun empty_work_address_fields(){
        viewModel.homeAddress.pincode = "999999"
        viewModel.homeAddress.addressLine1 = "Address Line 1"
        viewModel.homeAddress.city = "City"
        viewModel.homeAddress.state = "Uttarakhand"
        viewModel.addWorkAddress = true
        val result = viewModel.addressInfoValidation()
        Assert.assertEquals(false, result)
    }
}