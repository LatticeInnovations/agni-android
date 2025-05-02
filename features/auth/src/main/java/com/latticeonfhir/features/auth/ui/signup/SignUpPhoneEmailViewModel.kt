package com.latticeonfhir.features.auth.ui.signup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.data.server.enums.RegisterTypeEnum
import com.latticeonfhir.core.data.server.repository.signup.SignUpRepository
import com.latticeonfhir.core.utils.converters.responsemapper.ApiEmptyResponse
import com.latticeonfhir.core.utils.converters.responsemapper.ApiErrorResponse
import com.latticeonfhir.core.utils.regex.EmailRegex
import com.latticeonfhir.core.utils.regex.OnlyNumberRegex
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpPhoneEmailViewModel @Inject constructor(
    private val signUpRepository: SignUpRepository
) : ViewModel() {
    var isLaunched by mutableStateOf(false)
    var inputValue by mutableStateOf("")
    var isInputInvalid by mutableStateOf(true)
    var isAuthenticating by mutableStateOf(false)
    var isPhoneNumber by mutableStateOf(false)
    var isError by mutableStateOf(false)
    var errorMsg by mutableStateOf("")

    fun updateError() {
        errorMsg = "Enter valid phone number or Email address"
        isPhoneNumber = inputValue.matches(OnlyNumberRegex.onlyNumbers) && inputValue.length <= 10
        isInputInvalid = if (isPhoneNumber) {
            inputValue.length != 10
        } else !inputValue.matches(EmailRegex.emailPattern)
        isError = isInputInvalid
    }

    internal fun signUp(navigate: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            signUpRepository.verification(inputValue, RegisterTypeEnum.REGISTER).apply {
                if (this is ApiEmptyResponse) {
                    isAuthenticating = false
                    navigate(true)
                } else if (this is ApiErrorResponse) {
                    errorMsg = errorMessage
                    isError = true
                    isAuthenticating = false
                    navigate(false)
                }
            }
        }
    }

    fun isEnabled(): Boolean {
        return !isInputInvalid && inputValue.isNotEmpty()
    }
}