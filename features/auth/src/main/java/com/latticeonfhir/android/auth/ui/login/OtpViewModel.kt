package com.latticeonfhir.android.auth.ui.login

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.auth.data.server.model.authentication.TokenResponse
import com.latticeonfhir.android.auth.data.server.repository.authentication.AuthenticationRepository
import com.latticeonfhir.android.auth.data.server.repository.signup.SignUpRepository
import com.latticeonfhir.android.auth.utils.contants.ErrorConstants.TOO_MANY_ATTEMPTS_ERROR
import com.latticeonfhir.android.data.server.enums.RegisterTypeEnum
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEmptyResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEndResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiErrorResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ResponseMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtpViewModel @Inject constructor(
    application: Application,
    private val authenticationRepository: AuthenticationRepository,
    private val signUpRepository: SignUpRepository
) : AndroidViewModel(application) {
    var isLaunched by mutableStateOf(false)
    val otpValues = List(6) { mutableStateOf("") }
    val focusRequesters = List(6) { FocusRequester() }
    var userInput by mutableStateOf("")
    var twoMinuteTimer by mutableIntStateOf(120)
    var fiveMinuteTimer by mutableIntStateOf(0)
    var isVerifying by mutableStateOf(false)
    var isResending by mutableStateOf(false)
    var isOtpIncorrect by mutableStateOf(false)
    var otpEntered by mutableStateOf("")
    var errorMsg by mutableStateOf("")
    var otpAttemptsExpired by mutableStateOf(false)
    internal var isSignUp by mutableStateOf(false)
    internal lateinit var tempAuthToken: String

    fun updateOtp() {
        otpEntered = otpValues.joinToString(separator = "") { it.value }
    }

    internal fun resendOTP(resent: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            if (isSignUp) {
                signUpRepository.verification(userInput, RegisterTypeEnum.REGISTER)
                    .resentOtpApplyExtension(resent)
            } else {
                authenticationRepository.login(userInput).resentOtpApplyExtension(resent)
            }
        }
    }

    internal fun validateOtp(navigate: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            if (isSignUp) {
                signUpRepository.otpVerification(
                    userInput,
                    otpEntered.toInt(),
                    RegisterTypeEnum.REGISTER
                ).apply {
                    if (this is ApiEndResponse) {
                        tempAuthToken = body.token
                    }
                }.loginApplyExtension(navigate)
            } else {
                authenticationRepository.validateOtp(userInput, otpEntered.toInt())
                    .loginApplyExtension(navigate)
            }
        }
    }

    private fun ResponseMapper<TokenResponse>.loginApplyExtension(navigate: (Boolean) -> Unit) {
        apply {
            if (this is ApiEndResponse) {
                isVerifying = false
                navigate(true)
            } else if (this is ApiErrorResponse) {
                when (errorMessage) {
                    TOO_MANY_ATTEMPTS_ERROR -> {
                        isOtpIncorrect = false
                        otpAttemptsExpired = true
                        fiveMinuteTimer = 300
                    }

                    else -> isOtpIncorrect = true
                }
                isVerifying = false
                errorMsg = errorMessage
                navigate(false)
            }
        }
    }

    private fun ResponseMapper<String?>.resentOtpApplyExtension(resent: (Boolean) -> Unit) {
        apply {
            if (this is ApiEmptyResponse) {
                isResending = false
                resent(true)
            } else if (this is ApiErrorResponse) {
                when (errorMessage) {
                    TOO_MANY_ATTEMPTS_ERROR -> {
                        isOtpIncorrect = false
                        otpAttemptsExpired = true
                        fiveMinuteTimer = 300
                    }

                    else -> isOtpIncorrect = true
                }
                errorMsg = errorMessage
                isResending = false
                resent(false)
            }
        }
    }
}