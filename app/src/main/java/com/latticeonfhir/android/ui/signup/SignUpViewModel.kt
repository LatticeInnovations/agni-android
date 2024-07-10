package com.latticeonfhir.android.ui.signup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.server.model.register.Register
import com.latticeonfhir.android.data.server.repository.signup.SignUpRepository
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEndResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiErrorResponse
import com.latticeonfhir.android.utils.regex.EmailRegex
import com.latticeonfhir.android.utils.regex.OnlyNumberRegex
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(private val signUpRepository: SignUpRepository): BaseViewModel() {
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
                )
            ).apply {
                if(this is ApiErrorResponse) {
                    isError = true
                    navigate(false)
                } else if(this is ApiEndResponse) {
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