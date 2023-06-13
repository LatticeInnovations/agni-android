package com.latticeonfhir.android.viewmodel

import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.patient.PatientRepository
import com.latticeonfhir.android.ui.patienteditscreen.basicinfo.EditBasicInformationViewModel
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class EditBasicInformationViewModelTest {

    lateinit var viewModel: EditBasicInformationViewModel

    @Mock
    lateinit var patientRepository: PatientRepository

    @Mock
    lateinit var genericRepository: GenericRepository

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        viewModel = EditBasicInformationViewModel(
            patientRepository = patientRepository,
            genericRepository = genericRepository
        )
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
        assertEquals(true, result)
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
        assertEquals(true, result)
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
        assertEquals(true, result)
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
        assertEquals(false, result)
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
        assertEquals(false, result)
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
        assertEquals(false, result)
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
        assertEquals(false, result)
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
        assertEquals(false, result)
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
        assertEquals(false, result)
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
        assertEquals(false, result)
    }

    // check is Edit any field
    @Test
    fun checkIfAnyFieldEditReturnTrue() {
        // set data in fields
        viewModel.firstName = "Jhon"
        viewModel.middleName = ""
        viewModel.lastName = "Wick"
        viewModel.dobAgeSelector = "dob"
        viewModel.dobDay = "23"
        viewModel.dobMonth = "January"
        viewModel.dobYear = "2005"
        viewModel.gender = "Male"
        viewModel.phoneNumber = "111111111"
        viewModel.email = "john@gmail.com"

        // set data in tempField
        viewModel.firstNameTemp = viewModel.firstName
        viewModel.middleNameTemp = viewModel.middleName
        viewModel.lastNameTemp = viewModel.lastName
        viewModel.dobAgeSelectorTemp = viewModel.dobAgeSelector
        viewModel.dobDayTemp = viewModel.dobDay
        viewModel.dobMonthTemp = viewModel.dobMonth
        viewModel.dobYearTemp = viewModel.dobYear
        viewModel.genderTemp = viewModel.gender
        viewModel.phoneNumberTemp = viewModel.phoneNumber
        viewModel.emailTemp = viewModel.email

        // edit any field
        viewModel.middleName = "Carter"

        val isEdit = viewModel.checkIsEdit()
        assertEquals(true, isEdit)
    }

    @Test
    fun checkIfFieldNotEditReturnFalse() {
        // set data in fields
        viewModel.firstName = "Jhon"
        viewModel.middleName = ""
        viewModel.lastName = "Wick"
        viewModel.dobAgeSelector = "dob"
        viewModel.dobDay = "23"
        viewModel.dobMonth = "January"
        viewModel.dobYear = "2005"
        viewModel.gender = "Male"
        viewModel.phoneNumber = "111111111"
        viewModel.email = "john@gmail.com"

        // set data in tempField
        viewModel.firstNameTemp = viewModel.firstName
        viewModel.middleNameTemp = viewModel.middleName
        viewModel.lastNameTemp = viewModel.lastName
        viewModel.dobAgeSelectorTemp = viewModel.dobAgeSelector
        viewModel.dobDayTemp = viewModel.dobDay
        viewModel.dobMonthTemp = viewModel.dobMonth
        viewModel.dobYearTemp = viewModel.dobYear
        viewModel.genderTemp = viewModel.gender
        viewModel.phoneNumberTemp = viewModel.phoneNumber
        viewModel.emailTemp = viewModel.email


        val isEdit = viewModel.checkIsEdit()
        assertEquals(false, isEdit)
    }

}