package com.latticeonfhir.android.auth.ui.signup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.auth.data.server.model.register.Register
import com.latticeonfhir.core.auth.data.server.repository.signup.SignUpRepository
import com.latticeonfhir.core.auth.utils.regex.EmailRegex
import com.latticeonfhir.core.auth.utils.regex.OnlyNumberRegex
import com.latticeonfhir.android.utils.converters.responsemapper.ApiEndResponse
import com.latticeonfhir.core.utils.converters.server.responsemapper.ApiErrorResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(private val signUpRepository: SignUpRepository) :
    ViewModel() {
    var isLaunched by mutableStateOf(false)
    var inputName by mutableStateOf("")
    var inputClinicName by mutableStateOf("")
    var userInput by mutableStateOf("")
    var isInputInvalid by mutableStateOf(true)
    var isError by mutableStateOf(false)
    internal var isErrorName by mutableStateOf(false)
    internal var isErrorClinicName by mutableStateOf(false)
    var errorMsg by mutableStateOf("")
    var errorMessageName by mutableStateOf("")
    var errorMessageClinic by mutableStateOf("")
    internal lateinit var tempAuthToken: String

    fun updateError() {
        errorMessageName = "Enter valid name"
        errorMessageClinic = "Enter valid clinic name"
        isInputInvalid = userNameInputValidate() || clinicNameInputValidate()
        isErrorName = userNameInputValidate()
        isErrorClinicName = clinicNameInputValidate()
    }

    fun isEnabled(): Boolean {
        return !isInputInvalid && !isError
    }

    internal fun register(navigate: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            signUpRepository.register(
                register = Register(
                    firstName = inputName,
                    mobile = if (userInput.matches(OnlyNumberRegex.onlyNumbers) && userInput.length == 10) userInput else null,
                    email = if (userInput.matches(EmailRegex.emailPattern)) userInput else null,
                    clinicName = inputClinicName
                ),
                tempAuthToken = tempAuthToken
            ).apply {
                if (this is ApiErrorResponse) {
                    isError = true
                    navigate(false)
                } else if (this is ApiEndResponse) {
                    isError = false
                    navigate(true)
                }
            }
        }
    }

    private fun userNameInputValidate(): Boolean {
        return inputName.isEmpty() || inputName.length < 3 || inputName.length > 100
    }

    private fun clinicNameInputValidate(): Boolean {
        return inputClinicName.isEmpty() || inputClinicName.length < 3 || inputClinicName.length > 100
    }
}