package com.latticeonfhir.android.ui.login

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepository
import com.latticeonfhir.android.data.server.repository.authentication.AuthenticationRepository
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiEmptyResponse
import com.latticeonfhir.android.utils.converters.server.responsemapper.ApiErrorResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PhoneEmailViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
): BaseViewModel() {
    var inputValue by mutableStateOf("")
    var isInputValid by mutableStateOf(true)
    var isAuthenticating by mutableStateOf(false)
    var isPhoneNumber by mutableStateOf(false)
    var isError by mutableStateOf(false)
    var navigate by mutableStateOf(false)
    var errorMsg by mutableStateOf("")

    fun updateError() {
        errorMsg = "Enter valid phone number or Email address"
        isInputValid = if (isPhoneNumber) {
            inputValue.length != 10
        } else !Patterns.EMAIL_ADDRESS.matcher(inputValue).matches()
        isError = isInputValid
    }

    internal fun login(navigate:(Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            authenticationRepository.login(inputValue).apply {
                if(this is ApiEmptyResponse) {
                    //navigate = true
                    isAuthenticating = false
                    navigate(true)
                } else if(this is ApiErrorResponse) {
                    Timber.d("manseeyy $errorMessage")
                    errorMsg = errorMessage
                    isError = true
                    isAuthenticating = false
                    navigate(false)
                }
            }
        }
    }
}