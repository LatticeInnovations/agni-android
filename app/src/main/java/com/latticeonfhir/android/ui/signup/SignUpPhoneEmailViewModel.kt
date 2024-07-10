package com.latticeonfhir.android.ui.signup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepository
import com.latticeonfhir.android.data.local.roomdb.FhirAppDatabase
import com.latticeonfhir.android.data.server.enums.RegisterTypeEnum
import com.latticeonfhir.android.data.server.repository.authentication.AuthenticationRepository
import com.latticeonfhir.android.data.server.repository.signup.SignUpRepository
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEmptyResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiErrorResponse
import com.latticeonfhir.android.utils.regex.EmailRegex
import com.latticeonfhir.android.utils.regex.OnlyNumberRegex
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpPhoneEmailViewModel @Inject constructor(
    private val signUpRepository: SignUpRepository
) : BaseViewModel() {
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