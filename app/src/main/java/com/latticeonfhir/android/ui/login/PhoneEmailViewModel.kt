package com.latticeonfhir.android.ui.login

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.latticeonfhir.android.base.viewmodel.BaseViewModel

class PhoneEmailViewModel: BaseViewModel() {
    var inputValue by mutableStateOf("")
    var isInputValid by mutableStateOf(true)
    var isAuthenticating by mutableStateOf(false)
    var isOnlyNumber by mutableStateOf(false)
    var isError by mutableStateOf(false)
    var errorMsg by mutableStateOf("Enter valid phone number or Email address")

    fun updateError(){
        isError = if (isOnlyNumber){
            inputValue.length!=10
        } else !Patterns.EMAIL_ADDRESS.matcher(inputValue).matches()
    }
}