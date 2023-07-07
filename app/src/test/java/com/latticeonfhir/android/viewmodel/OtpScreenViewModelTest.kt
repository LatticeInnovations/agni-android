package com.latticeonfhir.android.viewmodel

import com.latticeonfhir.android.base.server.BaseResponse
import com.latticeonfhir.android.data.server.model.authentication.TokenResponse
import com.latticeonfhir.android.data.server.repository.authentication.AuthenticationRepository
import com.latticeonfhir.android.ui.login.OtpViewModel
import com.latticeonfhir.android.utils.converters.server.responsemapper.ResponseMapper
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class OtpScreenViewModelTest {
    @Mock
    lateinit var authenticationRepository: AuthenticationRepository
    lateinit var viewModel: OtpViewModel
    @OptIn(DelicateCoroutinesApi::class)
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
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
    fun test_resend_otp_on_authorised_number() = runTest {
        viewModel.userInput = "9876543210"
        `when`(authenticationRepository.login(viewModel.userInput)).thenReturn(
            ResponseMapper.create(
                Response.success(
                    BaseResponse(
                        1,
                        "Authorized user",
                        null,
                        null,
                        null,
                        null
                    )
                ),
                false
            )
        )
        viewModel.resendOTP {
            Assert.assertEquals("should return true on valid input", true, it)
        }
    }

    @Test
    fun test_resend_otp_on_unauthorised_number() = runTest {
        viewModel.userInput = "1234567890"
        `when`(authenticationRepository.login(viewModel.userInput)).thenReturn(
            ResponseMapper.create(
                Response.error(
                    401,
                    "{\"status\":0, \"message\":\"Unauthorized user\"}".toResponseBody("application/json".toMediaType())
                ),
                false
            )
        )
        viewModel.resendOTP {
            Assert.assertEquals("should return false on valid input", false, it)
        }
    }

    @Test
    fun test_resend_otp_on_too_many_attempts() = runTest {
        viewModel.userInput = "1234567890"
        `when`(authenticationRepository.login(viewModel.userInput)).thenReturn(
            ResponseMapper.create(
                Response.error(
                    401,
                    "{\"status\":0, \"message\":\"Too many attempts. Please try after 5 mins\"}".toResponseBody("application/json".toMediaType())
                ),
                false
            )
        )
        viewModel.resendOTP {
            Assert.assertEquals("should return false on valid input", false, it)
        }
    }

    @Test
    fun test_validate_otp_on_valid_otp() = runTest{
        viewModel.userInput = "9876543210"
        viewModel.otpEntered = "123456"
        `when`(authenticationRepository.validateOtp(viewModel.userInput, viewModel.otpEntered.toInt())).thenReturn(
            ResponseMapper.create(
                Response.success(
                    BaseResponse(
                        1,
                        "Logged in successfully",
                        null,
                        null,
                        TokenResponse(
                            "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjQsInVzZXJOYW1lIjoiRGV2IHRlc3QgMiIsImlhdCI6MTY4NzkzOTQ2NiwiZXhwIjoxNjg4MzcxNDY2fQ.x4K-GaQmSfFi8I-KKl1IYdUANBJTNDXxaX8fzJJE6mQ"
                        ),
                        null
                    )
                ),
                false
            )
        )
        viewModel.validateOtp {
            Assert.assertEquals("should return true on valid input", true, it)
        }
    }

    @Test
    fun test_validate_otp_on_invalid_otp() = runTest{
        viewModel.userInput = "9876543210"
        viewModel.otpEntered = "123456"
        `when`(authenticationRepository.validateOtp(viewModel.userInput, viewModel.otpEntered.toInt())).thenReturn(
            ResponseMapper.create(
                Response.error(
                    401,
                    "{\"status\":0, \"message\":\"Invalid OTP\"}".toResponseBody("application/json".toMediaType())
                ),
                false
            )
        )
        viewModel.validateOtp {
            Assert.assertEquals("should return true on valid input", true, it)
        }
    }

    @Test
    fun test_validate_otp_on_too_many_attempts_error() = runTest{
        viewModel.userInput = "9876543210"
        viewModel.otpEntered = "123456"
        `when`(authenticationRepository.validateOtp(viewModel.userInput, viewModel.otpEntered.toInt())).thenReturn(
            ResponseMapper.create(
                Response.error(
                    401,
                    "{\"status\":0, \"message\":\"Too many attempts. Please try after 5 mins\"}".toResponseBody("application/json".toMediaType())
                ),
                false
            )
        )
        viewModel.validateOtp {
            Assert.assertEquals("should return true on valid input", false, it)
        }
    }
}