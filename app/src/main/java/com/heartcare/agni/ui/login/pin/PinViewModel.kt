package com.heartcare.agni.ui.login.pin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import com.heartcare.agni.base.viewmodel.BaseViewModel

class PinViewModel : BaseViewModel() {
    val pinLength = 4

    var isLaunched by mutableStateOf(false)
    var screenFlag by mutableIntStateOf(0)

    var isLoading by mutableStateOf(false)

    val focusRequesters = List(4) { FocusRequester() }
    val pinValues = List(4) { mutableStateOf("") }
    var pinError by mutableStateOf(false)
}