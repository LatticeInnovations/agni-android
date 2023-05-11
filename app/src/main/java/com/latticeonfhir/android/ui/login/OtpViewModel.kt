package com.latticeonfhir.android.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.latticeonfhir.android.base.viewmodel.BaseViewModel

class OtpViewModel: BaseViewModel() {
    var isLaunched by mutableStateOf(false)
    var userInput by mutableStateOf("")
    var firstDigit by mutableStateOf("")
    var secondDigit by mutableStateOf("")
    var thirdDigit by mutableStateOf("")
    var fourDigit by mutableStateOf("")
    var fiveDigit by mutableStateOf("")
    var sixDigit by mutableStateOf("")
    var isOtpValid by mutableStateOf(false)
    var remainingTime by mutableStateOf(120)
    var isVerifying by mutableStateOf(false)
    var isOtpIncorrect by mutableStateOf(false)
    var otpEntered by mutableStateOf("")
    var errorMsg by mutableStateOf("")

    fun updateOtp(){
        otpEntered = ""
        otpEntered = (firstDigit+secondDigit+thirdDigit+fourDigit+fiveDigit+sixDigit).trim()
        isOtpValid = otpEntered.length == 6
    }
}