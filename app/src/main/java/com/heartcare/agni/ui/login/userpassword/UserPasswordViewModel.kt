package com.heartcare.agni.ui.login.userpassword

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.heartcare.agni.base.viewmodel.BaseViewModel

class UserPasswordViewModel : BaseViewModel() {
    val maxUserIdLength = 10
    val minUserIdLength = 3
    val maxPasswordLength = 15

    var userId by mutableStateOf("")
    var isUserIdError by mutableStateOf(false)
    var userIdError by mutableStateOf("")

    var password by mutableStateOf("")
    var isPasswordError by mutableStateOf(false)
    var passwordError by mutableStateOf("")
}