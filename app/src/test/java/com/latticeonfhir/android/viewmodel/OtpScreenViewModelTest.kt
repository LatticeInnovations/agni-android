package com.latticeonfhir.android.viewmodel

import com.latticeonfhir.android.data.server.repository.authentication.AuthenticationRepository
import com.latticeonfhir.android.ui.login.OtpViewModel
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiResponseConverter
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class OtpScreenViewModelTest {
    @Mock
    lateinit var authenticationRepository: AuthenticationRepository
    lateinit var viewModel: OtpViewModel
    @OptIn(DelicateCoroutinesApi::class)
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        viewModel = OtpViewModel(authenticationRepository)
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @Test
    fun enter_valid_input_digits() {
        viewModel.firstDigit = "1"
        viewModel.secondDigit = "2"
        viewModel.thirdDigit = "3"
        viewModel.fourDigit = "4"
        viewModel.fiveDigit = "5"
        viewModel.sixDigit = "6"
        viewModel.updateOtp()
        Assert.assertEquals("123456", viewModel.otpEntered)
    }

    @Test
    fun test_resend_otp(){
        viewModel.userInput = "9876543210"
        viewModel.resendOTP {
            Assert.assertEquals("should return true on valid input", true, it)
        }
    }

    @Test
    fun test_validate_otp(){
        viewModel.userInput = "9876543210"
        viewModel.otpEntered = "123456"
        viewModel.validateOtp {
            Assert.assertEquals("should return true on valid input", true, it)
        }
    }
}