package com.latticeonfhir.android.ui.signup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(): BaseViewModel() {
    var inputName by mutableStateOf("")
    var inputClinicName by mutableStateOf("")
    var isInputInvalid by mutableStateOf(true)
    var isError by mutableStateOf(false)
    var errorMsg by mutableStateOf("")

    fun updateError() {
        errorMsg = "Enter valid phone number or Email address"
        isInputInvalid = inputName.isEmpty().or(inputClinicName.isEmpty())
        isError = isInputInvalid
    }

    fun isEnabled(): Boolean {
        return !isInputInvalid && inputName.isNotEmpty() && inputClinicName.isNotEmpty()
    }

}