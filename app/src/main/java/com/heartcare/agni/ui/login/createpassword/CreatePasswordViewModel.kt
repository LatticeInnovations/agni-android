package com.heartcare.agni.ui.login.createpassword

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.heartcare.agni.base.viewmodel.BaseViewModel

class CreatePasswordViewModel : BaseViewModel() {
    val maxPasswordLength = 15

    var isLaunched by mutableStateOf(false)
    var screenFlag by mutableIntStateOf(0)

    var newPassword by mutableStateOf("")
    var isNewPasswordError by mutableStateOf(false)
    var isNewPasswordVisible by mutableStateOf(false)

    var confirmPassword by mutableStateOf("")
    var isConfirmPasswordError by mutableStateOf(false)
    var confirmPasswordError by mutableStateOf("")
    var isConfirmPasswordVisible by mutableStateOf(false)
    var hasInteractedWithConfirmPassword by mutableStateOf(false)

    fun validation(): Boolean {
        return newPassword.isNotBlank() && confirmPassword.isNotBlank() && !isNewPasswordError && !isConfirmPasswordError
    }
}