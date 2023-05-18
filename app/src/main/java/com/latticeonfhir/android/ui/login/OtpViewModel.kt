package com.latticeonfhir.android.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepository
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
    private val authenticationRepository: AuthenticationRepository,
    private val preferenceRepository: PreferenceRepository
) : BaseViewModel() {
    var isLaunched by mutableStateOf(false)
    var userInput by mutableStateOf("")
    var firstDigit by mutableStateOf("")
    var secondDigit by mutableStateOf("")
    var thirdDigit by mutableStateOf("")
    var fourDigit by mutableStateOf("")
    var fiveDigit by mutableStateOf("")
    var sixDigit by mutableStateOf("")
    var isOtpValid by mutableStateOf(false)
    var twoMinuteTimer by mutableStateOf(120)
    var fiveMinuteTimer by mutableStateOf(0)
    var isVerifying by mutableStateOf(false)
    var isResending by mutableStateOf(false)
    var isOtpIncorrect by mutableStateOf(false)
    var otpEntered by mutableStateOf("")
    var errorMsg by mutableStateOf("Invalid OTP, you have 4 attempts left")
    var otpAttemptsExpired by mutableStateOf(false)

    fun updateOtp() {
        otpEntered = ""
        otpEntered =
            (firstDigit + secondDigit + thirdDigit + fourDigit + fiveDigit + sixDigit).trim()
        isOtpValid = otpEntered.length == 6
    }

    internal fun resendOTP(resent:(Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            authenticationRepository.login(userInput).apply {
                if(this is ApiEmptyResponse) {
                    resent(true)
                } else if(this is ApiErrorResponse) {
                    errorMsg = errorMessage
                    isOtpIncorrect = true
                    resent(false)
                }
            }
        }
    }

    internal fun validateOtp(navigate: (Boolean) -> Unit) {
        viewModelScope.launch {
            authenticationRepository.validateOtp(userInput, otpEntered.toInt()).apply {
                if(this is ApiEndResponse) {
                    isVerifying = false
                    navigate(true)
                } else if(this is ApiErrorResponse){
                    when(errorMessage){
                        //OTP_EXPIRED -> ""
                        TOO_MANY_ATTEMPTS_ERROR -> {
                            //setTimeout()
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

    private fun setTimeout(){
        //preferenceRepository.setOtpAttemptTimeout(Date().time)
    }
}