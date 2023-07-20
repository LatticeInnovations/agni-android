package com.latticeonfhir.android.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.server.repository.authentication.AuthenticationRepository
import com.latticeonfhir.android.utils.constants.ErrorConstants.TOO_MANY_ATTEMPTS_ERROR
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEmptyResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEndResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiErrorResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtpViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : BaseViewModel() {
    var isLaunched by mutableStateOf(false)
    var userInput by mutableStateOf("")
    var firstDigit by mutableStateOf("")
    var secondDigit by mutableStateOf("")
    var thirdDigit by mutableStateOf("")
    var fourDigit by mutableStateOf("")
    var fiveDigit by mutableStateOf("")
    var sixDigit by mutableStateOf("")
    var twoMinuteTimer by mutableStateOf(120)
    var fiveMinuteTimer by mutableStateOf(0)
    var isVerifying by mutableStateOf(false)
    var isResending by mutableStateOf(false)
    var isOtpIncorrect by mutableStateOf(false)
    var otpEntered by mutableStateOf("")
    var errorMsg by mutableStateOf("")
    var otpAttemptsExpired by mutableStateOf(false)

    fun updateOtp() {
        otpEntered =
            (firstDigit + secondDigit + thirdDigit + fourDigit + fiveDigit + sixDigit).trim()
    }

    internal fun resendOTP(resent:(Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            authenticationRepository.login(userInput).apply {
                if(this is ApiEmptyResponse) {
                    isResending = false
                    resent(true)
                } else if(this is ApiErrorResponse) {
                    when(errorMessage){
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

    internal fun validateOtp(navigate: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            authenticationRepository.validateOtp(userInput, otpEntered.toInt()).apply {
                if(this is ApiEndResponse) {
                    isVerifying = false
                    navigate(true)
                } else if(this is ApiErrorResponse){
                    when(errorMessage){
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
    }
}