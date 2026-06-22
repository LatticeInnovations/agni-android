package com.latticeonfhir.android.ui.patientregistration.step2

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import com.latticeonfhir.android.base.viewmodel.BaseViewModel

class PatientRegistrationStepTwoViewModel : BaseViewModel(), DefaultLifecycleObserver {
    var isLaunched by mutableStateOf(false)

    val maxAbhaIdLength = 14
    val maxRationCardLength = 20

    var isAbhaSelected by mutableStateOf(false)
    var isRationCardSelected by mutableStateOf(false)
    var abhaId by mutableStateOf("")
    var rationCard by mutableStateOf("")

    val abhaRegex = Regex("^\\d{14}$")
    val rationCardRegex = Regex("^[A-Z0-9]{20}$")
    var isAbhaIdValid by mutableStateOf(false)
    var isRationCardValid by mutableStateOf(false)

    fun identityInfoValidation(): Boolean {
        if (isAbhaSelected && !abhaRegex.matches(abhaId))
            return false
        if (isRationCardSelected && !rationCardRegex.matches(rationCard))
            return false
        return true
    }
}