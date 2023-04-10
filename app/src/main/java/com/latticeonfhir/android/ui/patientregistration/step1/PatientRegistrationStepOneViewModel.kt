package com.latticeonfhir.android.ui.patientregistration.step1

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import com.latticeonfhir.android.base.viewmodel.BaseViewModel

class PatientRegistrationStepOneViewModel: BaseViewModel(), DefaultLifecycleObserver {
    var isLaunched by mutableStateOf(false)

    val maxFirstNameLength = 150
    val maxMiddleNameLength = 150
    val maxLastNameLength = 150
    val maxEmailLength = 150

    var firstName by mutableStateOf("")
    var middleName by mutableStateOf("")
    var lastName by mutableStateOf("")
    var phoneNumber by mutableStateOf("")
    var email by mutableStateOf("")
    var dobAgeSelector by mutableStateOf("dob")
    var dobDay by mutableStateOf("")
    var dobMonth by mutableStateOf("")
    var dobYear by mutableStateOf("")
    var years by mutableStateOf("")
    var months by mutableStateOf("")
    var days by mutableStateOf("")
    var gender by mutableStateOf("")

    val daysList = (1..31).toList()
    val monthsList = listOf("January", "February", "March", "April", "May", "June",
                        "July", "August", "September", "October", "November", "December")

    val yearsList = (1920..2023).toList()

    var isNameValid by mutableStateOf(false)
    var isEmailValid by mutableStateOf(false)
    var isPhoneValid by mutableStateOf(false)

    fun basicInfoValidation(): Boolean{
        if (firstName.length < 3 || firstName.length > 150)
            return false
        if (middleName.length > 150 || lastName.length > 150)
            return false
        if (dobAgeSelector == "dob" && (dobDay == "" || dobYear == "" || dobMonth ==""))
            return false
        if (dobAgeSelector == "age" &&
            (years == "" ||
                    months.toInt() < 0 || months.toInt() > 12 ||
                    days.toInt() < 0 || days.toInt() > 31))
            return false
        if (phoneNumber.length < 10)
            return false
        if(email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return false
        if (gender == "")
            return false
        return true
    }
}