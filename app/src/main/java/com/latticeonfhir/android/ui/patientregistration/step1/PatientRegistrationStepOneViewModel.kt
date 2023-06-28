package com.latticeonfhir.android.ui.patientregistration.step1

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import com.latticeonfhir.android.base.viewmodel.BaseViewModel

class PatientRegistrationStepOneViewModel: BaseViewModel(), DefaultLifecycleObserver {
    var isLaunched by mutableStateOf(false)

    val onlyNumbers = Regex("^\\d+\$")

    val maxFirstNameLength = 100
    val maxMiddleNameLength = 100
    val maxLastNameLength = 100
    val maxEmailLength = 100

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
    var monthsList = mutableStateListOf("January", "February", "March", "April", "May", "June",
                        "July", "August", "September", "October", "November", "December")

    fun getMonthsList(){
        monthsList =
            if (dobDay.toInt()>30) mutableStateListOf("January", "March", "May", "July", "August", "October", "December")
            else if (dobDay.toInt()>29) mutableStateListOf("January", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December")
            else mutableStateListOf("January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December")
    }
    val yearsList = (1920..2023).toList()

    var isNameValid by mutableStateOf(false)
    var isEmailValid by mutableStateOf(false)
    var isPhoneValid by mutableStateOf(false)
    var isDobDayValid by mutableStateOf(false)
    var isDobMonthValid by mutableStateOf(false)
    var isDobYearValid by mutableStateOf(false)
    var isAgeDaysValid by mutableStateOf(false)
    var isAgeMonthsValid by mutableStateOf(false)
    var isAgeYearsValid by mutableStateOf(false)

    fun basicInfoValidation(): Boolean{
        if (firstName.length < 3 || firstName.length > 100)
            return false
        if (middleName.length > 100 || lastName.length > 100)
            return false
        if (dobAgeSelector == "dob" && (isDobDayValid || isDobMonthValid || isDobYearValid))
            return false
        if (dobAgeSelector == "age" && (isAgeDaysValid || isAgeMonthsValid || isAgeYearsValid))
            return false
        if (phoneNumber.length != 10)
            return false
        if(email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return false
        if (gender == "")
            return false
        return true
    }
}