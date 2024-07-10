package com.latticeonfhir.android.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepository
import com.latticeonfhir.android.data.local.roomdb.FhirAppDatabase
import com.latticeonfhir.android.data.server.enums.RegisterTypeEnum
import com.latticeonfhir.android.data.server.model.authentication.TokenResponse
import com.latticeonfhir.android.data.server.repository.authentication.AuthenticationRepository
import com.latticeonfhir.android.data.server.repository.signup.SignUpRepository
import com.latticeonfhir.android.utils.constants.ErrorConstants.TOO_MANY_ATTEMPTS_ERROR
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
    private val authenticationRepository: AuthenticationRepository,
    private val signUpRepository: SignUpRepository,
    private val fhirAppDatabase: FhirAppDatabase,
    private val preferenceRepository: PreferenceRepository
) : BaseViewModel() {
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
    internal var isDeleteAccount by mutableStateOf(false)
    internal var logoutReason by mutableStateOf("")

    fun updateOtp() {
        otpEntered = otpValues.joinToString(separator = "") { it.value }
    }

    internal fun resendOTP(resent: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            if(isSignUp) {
                signUpRepository.verification(userInput, RegisterTypeEnum.REGISTER).resentOtpApplyExtension(resent)
            } else if(isDeleteAccount) {
                signUpRepository.verification(userInput, RegisterTypeEnum.DELETE).resentOtpApplyExtension(resent)
            } else {
                authenticationRepository.login(userInput).resentOtpApplyExtension(resent)
            }
        }
    }

    internal fun validateOtp(navigate: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            if(isSignUp) {
                signUpRepository.otpVerification(userInput, otpEntered.toInt(), RegisterTypeEnum.REGISTER).apply {
                    if(this is ApiEndResponse) {
                        preferenceRepository.setAuthenticationToken(body.token)
                    }
                }.loginApplyExtension(navigate)
            } else if(isDeleteAccount) {
                val otpVerifyResponse = signUpRepository.otpVerification(userInput, otpEntered.toInt(), RegisterTypeEnum.DELETE)
                if (otpVerifyResponse is ApiEndResponse) {
                    isVerifying = false
                    deleteAccount(otpVerifyResponse.body.token, navigate)
                } else {
                    otpVerifyResponse.loginApplyExtension(navigate)
                }
            } else {
                authenticationRepository.validateOtp(userInput, otpEntered.toInt()).loginApplyExtension(navigate)
            }
        }
    }

    private suspend fun deleteAccount(tempAuthToken: String, navigate: (Boolean) -> Unit) {
        authenticationRepository.deleteAccount(tempAuthToken).apply {
            if(this is ApiErrorResponse) {
                errorMsg = errorMessage
                navigate(false)
            } else if (this is ApiEndResponse) {
                logoutReason = body ?: "INTERNAL_SERVER_ERROR"
                clearAllAppData()
                navigate(true)
            }
        }
    }

    private fun clearAllAppData() {
        fhirAppDatabase.clearAllTables()
        preferenceRepository.clearPreferences()
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