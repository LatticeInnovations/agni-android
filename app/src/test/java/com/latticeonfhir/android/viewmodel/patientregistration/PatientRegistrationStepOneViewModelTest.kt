package com.latticeonfhir.android.viewmodel.patientregistration

import android.util.Patterns
import com.latticeonfhir.android.ui.patientregistration.step1.PatientRegistrationStepOneViewModel
import com.latticeonfhir.android.utils.converters.responseconverter.MonthsList
import junit.framework.TestCase.assertEquals
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.util.regex.Pattern

class PatientRegistrationStepOneViewModelTest {

    @Mock
    private lateinit var patterns: Patterns

    companion object {
        const val EMAIL = "sumojija"
    }

    private lateinit var viewModel: PatientRegistrationStepOneViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
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

    @Test
    fun middle_name_more_than_100() {
        viewModel.firstName = "Ramesh"
        viewModel.middleName =
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

    @Test
    fun last_name_more_than_100() {
        viewModel.firstName = "Ramesh"
        viewModel.lastName =
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

    @Test
    fun `get month list for 31 days month`() {
        viewModel.dobDay = "31"
        viewModel.monthsList = MonthsList.getMonthsList(viewModel.dobDay)
        assertEquals(false,viewModel.monthsList.toList().contains("June"))
    }

    @Test
    fun `get month list for 30 days month`() {
        viewModel.dobDay = "30"
        viewModel.monthsList = MonthsList.getMonthsList(viewModel.dobDay)
        assertEquals(false,viewModel.monthsList.toList().contains("February"))
    }

    @Test
    fun `get month list for 29 days month`() {
        viewModel.dobDay = "29"
        viewModel.monthsList = MonthsList.getMonthsList(viewModel.dobDay)
        assertEquals(12,viewModel.monthsList.toList().size)
    }

    @Test
    fun `dob day in_valid`() {
        viewModel.firstName = "mansi"
        viewModel.dobAgeSelector = "dob"
        viewModel.dobDay = "23"
        viewModel.dobMonth = "January"
        viewModel.dobYear = "2001"
        viewModel.gender = "male"
        viewModel.phoneNumber = "9876543210"

        viewModel.isDobDayValid = true
        val result = viewModel.basicInfoValidation()
        assertEquals(false,result)
    }

    @Test
    fun `dob month in_valid`() {
        viewModel.firstName = "mansi"
        viewModel.dobAgeSelector = "dob"
        viewModel.dobDay = "23"
        viewModel.dobMonth = "January"
        viewModel.dobYear = "2001"
        viewModel.gender = "male"
        viewModel.phoneNumber = "9876543210"

        viewModel.isDobMonthValid = true
        val result = viewModel.basicInfoValidation()
        assertEquals(false,result)
    }

    @Test
    fun `dob year in_valid`() {
        viewModel.firstName = "mansi"
        viewModel.dobAgeSelector = "dob"
        viewModel.dobDay = "23"
        viewModel.dobMonth = "January"
        viewModel.dobYear = "2001"
        viewModel.gender = "male"
        viewModel.phoneNumber = "9876543210"

        viewModel.isDobYearValid = true
        val result = viewModel.basicInfoValidation()
        assertEquals(false,result)
    }

    @Test
    fun `age day in_valid`() {
        viewModel.firstName = "mansi"
        viewModel.dobAgeSelector = "age"
        viewModel.dobDay = "23"
        viewModel.dobMonth = "January"
        viewModel.dobYear = "2001"
        viewModel.gender = "male"
        viewModel.phoneNumber = "9876543210"

        viewModel.isAgeDaysValid = true
        val result = viewModel.basicInfoValidation()
        assertEquals(false,result)
    }

    @Test
    fun `age month in_valid`() {
        viewModel.firstName = "mansi"
        viewModel.dobAgeSelector = "age"
        viewModel.dobDay = "23"
        viewModel.dobMonth = "January"
        viewModel.dobYear = "2001"
        viewModel.gender = "male"
        viewModel.phoneNumber = "9876543210"

        viewModel.isAgeMonthsValid = true
        val result = viewModel.basicInfoValidation()
        assertEquals(false,result)
    }

    @Test
    fun `age year in_valid`() {
        viewModel.firstName = "mansi"
        viewModel.dobAgeSelector = "age"
        viewModel.dobDay = "23"
        viewModel.dobMonth = "January"
        viewModel.dobYear = "2001"
        viewModel.gender = "male"
        viewModel.phoneNumber = "9876543210"

        viewModel.isAgeYearsValid = true
        val result = viewModel.basicInfoValidation()
        assertEquals(false,result)
    }
}