package com.latticeonfhir.android.viewmodel

import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepository
import com.latticeonfhir.android.data.server.repository.authentication.AuthenticationRepository
import com.latticeonfhir.android.ui.login.PhoneEmailViewModel
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class PhoneEmailViewModelTest {
    @Mock
    lateinit var authenticationRepository: AuthenticationRepository
    lateinit var viewModel: PhoneEmailViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        viewModel = PhoneEmailViewModel(authenticationRepository)
    }

    @Test
    fun enter_valid_phone_number() {
        viewModel.inputValue = "9876543210"
        viewModel.updateError()
        Assert.assertEquals("isPhoneNumber should be true, on valid phone number input", true, viewModel.isPhoneNumber)
        Assert.assertEquals("isInputInvalid should be false, on valid input", false, viewModel.isInputInvalid)
        Assert.assertEquals("isError should be false, on valid input", false, viewModel.isError)
    }

    @Test
    fun enter_valid_email() {
        viewModel.inputValue = "mansi@thelattice.in"
        viewModel.updateError()
        Assert.assertEquals("isPhoneNumber should be false, on valid email input", false, viewModel.isPhoneNumber)
        Assert.assertEquals("isInputInvalid should be false, on valid input", false, viewModel.isInputInvalid)
        Assert.assertEquals("isError should be false, on valid input", false, viewModel.isError)
    }

    @Test
    fun enter_phone_number_length_less_than_10 () {
        viewModel.inputValue = "123456789"
        viewModel.updateError()
        Assert.assertEquals("isPhoneNumber should be true, on only numbers input", true, viewModel.isPhoneNumber)
        Assert.assertEquals("isInputInvalid should be true, on invalid input", true, viewModel.isInputInvalid)
        Assert.assertEquals("isError should be true, on invalid input", true, viewModel.isError)
    }

    @Test
    fun enter_phone_number_length_more_than_10 () {
        viewModel.inputValue = "12345678910"
        viewModel.updateError()
        Assert.assertEquals("isPhoneNumber should be false, on invalid phone number input", false, viewModel.isPhoneNumber)
        Assert.assertEquals("isInputInvalid should be true, on invalid input", true, viewModel.isInputInvalid)
        Assert.assertEquals("isError should be true, on invalid input", true, viewModel.isError)
    }

    @Test
    fun enter_alphanumeric_input () {
        viewModel.inputValue = "123456abcdef"
        viewModel.updateError()
        Assert.assertEquals("isPhoneNumber should be false, on alphanumeric input", false, viewModel.isPhoneNumber)
        Assert.assertEquals("isInputInvalid should be true, on invalid input", true, viewModel.isInputInvalid)
        Assert.assertEquals("isError should be true, on invalid input", true, viewModel.isError)
    }

    @Test
    fun enter_invalid_email () {
        viewModel.inputValue = "abc.com"
        viewModel.updateError()
        Assert.assertEquals("isPhoneNumber should be false, on email input", false, viewModel.isPhoneNumber)
        Assert.assertEquals("isInputInvalid should be true, on invalid input", true, viewModel.isInputInvalid)
        Assert.assertEquals("isError should be true, on invalid input", true, viewModel.isError)
    }

    @Test
    fun login_on_authorised_email () {
        viewModel.inputValue = "mansi@thelattice.in"
        viewModel.login {
            Assert.assertEquals("returned value should be true on valid authorised input", true, it)
            Assert.assertEquals("isError should be false when logging in with authorised email", false, viewModel.isError)
        }
    }

    @Test
    fun login_on_unauthorised_email () {
        viewModel.inputValue = "mansi@gmail.com"
        viewModel.login {
            Assert.assertEquals("returned value should be true on valid unauthorised input", false, it)
            Assert.assertEquals("error message should ne 'Unauthorised user'.", "Unauthorised user", viewModel.errorMsg)
            Assert.assertEquals("isError should be true when logging in with unauthorised email", true, viewModel.isError)
        }
    }

    @Test
    fun login_on_authorised_phone () {
        viewModel.inputValue = "9876543210"
        viewModel.login {
            Assert.assertEquals("returned value should be true on valid authorised input", true, it)
            Assert.assertEquals("isError should be false when logging in with authorised email", false, viewModel.isError)
        }
    }

    @Test
    fun login_on_unauthorised_phone () {
        viewModel.inputValue = "98766543210"
        viewModel.login {
            Assert.assertEquals("returned value should be true on valid unauthorised input", false, it)
            Assert.assertEquals("error message should ne 'Unauthorised user'.", "Unauthorised user", viewModel.errorMsg)
            Assert.assertEquals("isError should be true when logging in with unauthorised phone", true, viewModel.isError)
        }
    }
}