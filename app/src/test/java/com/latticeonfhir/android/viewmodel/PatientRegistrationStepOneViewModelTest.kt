package com.latticeonfhir.android.viewmodel

import com.latticeonfhir.android.ui.patientregistration.step1.PatientRegistrationStepOneViewModel
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class PatientRegistrationStepOneViewModelTest {

    lateinit var viewModel: PatientRegistrationStepOneViewModel

    @Before
    fun setUp() {
        viewModel = PatientRegistrationStepOneViewModel()
    }

    @Test
    fun valid_inputs_with_required_fields_and_dob() {
        viewModel.firstName = "mansi"
        viewModel.dobAgeSelector = "dob"
        viewModel.dobDay = "23"
        viewModel.dobMonth = "January"
        viewModel.dobYear = "2001"
        viewModel.gender = "female"
        viewModel.phoneNumber = "9999999999"
        val result = viewModel.basicInfoValidation()
        Assert.assertEquals(true, result)
    }

    @Test
    fun valid_inputs_with_required_fields_and_age() {
        viewModel.firstName = "mansi"
        viewModel.dobAgeSelector = "age"
        viewModel.years = "23"
        viewModel.months = "5"
        viewModel.days = "20"
        viewModel.gender = "female"
        viewModel.phoneNumber = "9999999999"
        val result = viewModel.basicInfoValidation()
        Assert.assertEquals(true, result)
    }

    @Test
    fun valid_inputs_with_all_fields() {
        viewModel.firstName = "mansi"
        viewModel.middleName = "m"
        viewModel.lastName = "kalra"
        viewModel.dobAgeSelector = "age"
        viewModel.years = "23"
        viewModel.months = "5"
        viewModel.days = "20"
        viewModel.gender = "female"
        viewModel.phoneNumber = "9999999999"
        val result = viewModel.basicInfoValidation()
        Assert.assertEquals(true, result)
    }

    // invalid first name
    @Test
    fun first_name_less_than_3() {
        viewModel.firstName = "ma"
        viewModel.dobAgeSelector = "dob"
        viewModel.dobDay = "23"
        viewModel.dobMonth = "January"
        viewModel.dobYear = "2001"
        viewModel.gender = "female"
        viewModel.phoneNumber = "9999999999"
        val result = viewModel.basicInfoValidation()
        Assert.assertEquals(false, result)
    }

    @Test
    fun first_name_empty() {
        viewModel.firstName = ""
        viewModel.dobAgeSelector = "dob"
        viewModel.dobDay = "23"
        viewModel.dobMonth = "January"
        viewModel.dobYear = "2001"
        viewModel.gender = "female"
        viewModel.phoneNumber = "9999999999"
        val result = viewModel.basicInfoValidation()
        Assert.assertEquals(false, result)
    }

    @Test
    fun first_name_more_than_100() {
        viewModel.firstName =
            "kmnxdsccccccccccccccccccccnjsdcnjacsxhjzanchdjxabnhcdsjbnchjdsbnchdsjbdhcsjbchjbhcdsbcdhsb cdshjbscdzhjbcxdshbcd sbcds hsbadhjdsajhnsakjncsajkskdhskjhdshsdkjhcdnskjhdjhsdhjsahjhasbjhsdbjhsajsahjsdkhcdskjhncdsjhjheskjsaihfduyshfbshdjbfdshjbedshjdbsjdshjbdfshjbedsfhjbdshjdsbjhdsgbhjds"
        viewModel.dobAgeSelector = "dob"
        viewModel.dobDay = "23"
        viewModel.dobMonth = "January"
        viewModel.dobYear = "2001"
        viewModel.gender = "female"
        viewModel.phoneNumber = "9999999999"
        val result = viewModel.basicInfoValidation()
        Assert.assertEquals(false, result)
    }

    // invalid phone number
    @Test
    fun phone_number_more_than_10() {
        viewModel.firstName = "mansi"
        viewModel.dobAgeSelector = "dob"
        viewModel.dobDay = "23"
        viewModel.dobMonth = "January"
        viewModel.dobYear = "2001"
        viewModel.gender = "female"
        viewModel.phoneNumber = "99999999999"
        val result = viewModel.basicInfoValidation()
        Assert.assertEquals(false, result)
    }

    @Test
    fun phone_number_less_than_10() {
        viewModel.firstName = "mansi"
        viewModel.dobAgeSelector = "dob"
        viewModel.dobDay = "23"
        viewModel.dobMonth = "January"
        viewModel.dobYear = "2001"
        viewModel.gender = "female"
        viewModel.phoneNumber = "999999999"
        val result = viewModel.basicInfoValidation()
        Assert.assertEquals(false, result)
    }

    @Test
    fun phone_number_null() {
        viewModel.firstName = "mansi"
        viewModel.dobAgeSelector = "dob"
        viewModel.dobDay = "23"
        viewModel.dobMonth = "January"
        viewModel.dobYear = "2001"
        viewModel.gender = "female"
        viewModel.phoneNumber = ""
        val result = viewModel.basicInfoValidation()
        Assert.assertEquals(false, result)
    }

    // not selecting gender
    @Test
    fun gender_null() {
        viewModel.firstName = "mansi"
        viewModel.dobAgeSelector = "dob"
        viewModel.dobDay = "23"
        viewModel.dobMonth = "January"
        viewModel.dobYear = "2001"
        viewModel.gender = ""
        viewModel.phoneNumber = "9876543210"
        val result = viewModel.basicInfoValidation()
        Assert.assertEquals(false, result)
    }

}