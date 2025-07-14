package com.heartcare.agni.ui.login.forgotpassword

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.utils.regex.EmailRegex
import com.heartcare.agni.utils.regex.OnlyNumberRegex

class ForgotPasswordViewModel: BaseViewModel() {
    var inputValue by mutableStateOf("")
    var isPhoneNumber by mutableStateOf(false)
    var isError by mutableStateOf(false)

    fun updateError() {
        isPhoneNumber = inputValue.matches(OnlyNumberRegex.onlyNumbers) && inputValue.length <= 15
        isError = if (isPhoneNumber) {
            inputValue.length != 15
        } else !inputValue.matches(EmailRegex.emailPattern)
    }
}