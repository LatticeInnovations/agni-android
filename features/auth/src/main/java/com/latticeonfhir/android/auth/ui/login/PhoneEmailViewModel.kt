package com.latticeonfhir.android.auth.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.core.auth.data.server.repository.authentication.AuthenticationRepository
import com.latticeonfhir.core.auth.utils.contants.ErrorConstants.USER_DOES_NOT_EXIST
import com.latticeonfhir.core.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepository
import com.latticeonfhir.android.data.local.roomdb.FhirAppDatabase
import com.latticeonfhir.core.data.server.repository.authentication.AuthenticationRepository
import com.latticeonfhir.core.utils.constants.ErrorConstants.USER_DOES_NOT_EXIST
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEmptyResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiErrorResponse
import com.latticeonfhir.core.utils.regex.EmailRegex
import com.latticeonfhir.core.utils.regex.OnlyNumberRegex
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhoneEmailViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val preferenceRepository: PreferenceRepository,
    private val fhirAppDatabase: FhirAppDatabase
) : ViewModel() {
    var isLaunched by mutableStateOf(false)
    var inputValue by mutableStateOf("")
    private var isInputInvalid by mutableStateOf(true)
    var isAuthenticating by mutableStateOf(false)
    var isPhoneNumber by mutableStateOf(false)
    var isError by mutableStateOf(false)
    var errorMsg by mutableStateOf("")
    var showDifferentUserLoginDialog by mutableStateOf(false)
    internal var signUpButtonIsVisible by mutableStateOf(false)

    fun updateError() {
        errorMsg = "Enter valid phone number or Email address"
        isPhoneNumber = inputValue.matches(OnlyNumberRegex.onlyNumbers) && inputValue.length <= 10
        isInputInvalid = if (isPhoneNumber) {
            inputValue.length != 10
        } else !inputValue.matches(EmailRegex.emailPattern)
        isError = isInputInvalid
    }

    internal fun login(navigate: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            authenticationRepository.login(inputValue).apply {
                if (this is ApiEmptyResponse) {
                    signUpButtonIsVisible = false
                    isAuthenticating = false
                    navigate(true)
                } else if (this is ApiErrorResponse) {
                    if (errorMessage == USER_DOES_NOT_EXIST) signUpButtonIsVisible = true
                    errorMsg = errorMessage
                    isError = true
                    isAuthenticating = false
                    navigate(false)
                }
            }
        }
    }

    fun isDifferentUserLogin(): Boolean {
        return if (preferenceRepository.getUserMobile() == 0L && preferenceRepository.getUserEmail()
                .isBlank()
        ) {
            false
        } else !(preferenceRepository.getUserEmail() == inputValue || preferenceRepository.getUserMobile()
            .toString() == inputValue)
    }

    internal fun clearAllAppData() {
        viewModelScope.launch(Dispatchers.IO) {
            fhirAppDatabase.clearAllTables()
            preferenceRepository.clearPreferences()
        }
    }

    fun isEnabled(): Boolean {
        return !isInputInvalid && inputValue.isNotEmpty()
    }
}