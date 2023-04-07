package com.latticeonfhir.android.ui.main.patientregistration.step2

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import com.latticeonfhir.android.base.viewmodel.BaseViewModel

class PatientRegistrationStepTwoViewModel: BaseViewModel(), DefaultLifecycleObserver {
    var isLaunched by mutableStateOf(false)

    val maxPassportIdLength = 8
    val maxVoterIdLength = 10
    val maxPatientIdLength = 10

    var isPassportSelected by mutableStateOf(true)
    var isVoterSelected by mutableStateOf(false)
    var isPatientSelected by mutableStateOf(false)
    var passportId by mutableStateOf("")
    var voterId by mutableStateOf("")
    var patientId by mutableStateOf("")

    val passportPattern = Regex("^[A-PR-WYa-pr-wy][1-9]\\d\\s?\\d{4}[1-9]$")
    val voterPattern = Regex("^[A-Za-z]{3}[0-9]{7}$")
    var isPassportValid by mutableStateOf(false)
    var isVoterValid by mutableStateOf(false)
    var isPatientValid by mutableStateOf(false)

    fun identityInfoValidation(): Boolean{
        if (isPassportSelected == false && isVoterSelected == false && isPatientSelected == false)
            return false
        if (isPassportSelected && !passportPattern.matches(passportId))
            return false
        if (isVoterSelected && !voterPattern.matches(voterId))
            return false
        if(isPatientSelected && patientId.length<10)
            return false
        return true
    }
}