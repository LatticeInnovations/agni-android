package com.latticeonfhir.android.ui.patientregistration.step1

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import com.latticeonfhir.android.base.viewmodel.BaseViewModel

class PatientRegistrationStepOneViewModel : BaseViewModel(), DefaultLifecycleObserver {
    internal var isLaunched by mutableStateOf(false)

    internal val onlyNumbers = Regex("^\\d+\$")

    internal val maxFirstNameLength = 100
    internal val maxMiddleNameLength = 100
    internal val maxLastNameLength = 100
    internal val maxEmailLength = 100

    internal var firstName by mutableStateOf("")
    internal var middleName by mutableStateOf("")
    internal var lastName by mutableStateOf("")
    internal var phoneNumber by mutableStateOf("")
    internal var email by mutableStateOf("")
    internal var dobAgeSelector by mutableStateOf("dob")
    internal var dobDay by mutableStateOf("")
    internal var dobMonth by mutableStateOf("")
    internal var dobYear by mutableStateOf("")
    internal var years by mutableStateOf("")
    internal var months by mutableStateOf("")
    internal var days by mutableStateOf("")
    internal var gender by mutableStateOf("")

    internal var monthsList = mutableStateListOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    internal fun getMonthsList() {
        monthsList =
            if (dobDay.toInt() > 30) mutableStateListOf(
                "January",
                "March",
                "May",
                "July",
                "August",
                "October",
                "December"
            )
            else if (dobDay.toInt() > 29) mutableStateListOf(
                "January", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
            )
            else mutableStateListOf(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
            )
    }

    internal var isNameValid by mutableStateOf(false)
    internal var isEmailValid by mutableStateOf(false)
    internal var isPhoneValid by mutableStateOf(false)
    internal var isDobDayValid by mutableStateOf(false)
    internal var isDobMonthValid by mutableStateOf(false)
    internal var isDobYearValid by mutableStateOf(false)
    internal var isAgeDaysValid by mutableStateOf(false)
    internal var isAgeMonthsValid by mutableStateOf(false)
    internal var isAgeYearsValid by mutableStateOf(false)

    internal fun basicInfoValidation(): Boolean {
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
        if (email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return false
        if (gender == "")
            return false
        return true
    }
}