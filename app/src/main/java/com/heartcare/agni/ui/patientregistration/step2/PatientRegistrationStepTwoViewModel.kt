package com.heartcare.agni.ui.patientregistration.step2

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import com.heartcare.agni.base.viewmodel.BaseViewModel

class PatientRegistrationStepTwoViewModel : BaseViewModel(), DefaultLifecycleObserver {
    var isLaunched by mutableStateOf(false)

    val maxHospitalIdLength = 6
    val maxNationalIdLength = 6

    var isPassportSelected by mutableStateOf(false)
    var isVoterSelected by mutableStateOf(false)
    var isPatientSelected by mutableStateOf(false)
    var passportId by mutableStateOf("")
    var voterId by mutableStateOf("")
    var patientId by mutableStateOf("")
    var hospitalId by mutableStateOf("")
    var nationalId by mutableStateOf("")
    var isVerifyClicked by mutableStateOf(false)
    var isNationalIdVerified by mutableStateOf(false)
    var isHospitalIdValid by mutableStateOf(false)

    fun identityInfoValidation(): Boolean {
        if (isHospitalIdValid)
            return false
        return isVerifyClicked || nationalId.isBlank()
    }
}