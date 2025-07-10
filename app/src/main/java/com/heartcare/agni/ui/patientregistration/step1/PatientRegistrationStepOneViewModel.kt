package com.heartcare.agni.ui.patientregistration.step1

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toMonthInteger

class PatientRegistrationStepOneViewModel : BaseViewModel(), DefaultLifecycleObserver {
    internal var isLaunched by mutableStateOf(false)

    internal val onlyNumbers = Regex("^\\d+\$")

    internal val maxNameLength = 50
    internal val maxPhoneNumberLength = 15

    internal var firstName by mutableStateOf("")
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
    internal var isPersonDeceased by mutableIntStateOf(0)
    var showDeceasedReasonSheet by mutableStateOf(false)
    var selectedDeceasedReason by mutableStateOf("")

    internal var motherName by mutableStateOf("")
    internal var fatherName by mutableStateOf("")
    internal var spouseName by mutableStateOf("")

    internal var monthsList = mutableStateListOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    internal var isMotherNameValid by mutableStateOf(false)
    internal var isLastNameValid by mutableStateOf(false)
    internal var isFirstNameValid by mutableStateOf(false)
    internal var isPhoneValid by mutableStateOf(false)
    internal var isAgeDaysValid by mutableStateOf(false)
    internal var isAgeMonthsValid by mutableStateOf(false)
    internal var isAgeYearsValid by mutableStateOf(false)

    internal fun basicInfoValidation(): Boolean {
        return firstName.isNotBlank() &&
                lastName.isNotBlank() &&
                motherName.isNotBlank() &&
                !verifyDOB() &&
                !verifyAge() &&
                !isPhoneValid &&
                gender.isNotBlank()
    }

    private fun verifyDOB(): Boolean{
        return dobAgeSelector == "dob" && ((dobDay.isBlank() || dobMonth.isBlank() || dobYear.isBlank()) || (!TimeConverter.isDOBValid(
            dobDay.toInt(),
            dobMonth.toMonthInteger(),
            dobYear.toInt()
        )))
    }

    private fun verifyAge(): Boolean{
        return dobAgeSelector == "age" && ((years.isBlank() && months.isBlank() && days.isBlank()) || (isAgeYearsValid || isAgeDaysValid || isAgeMonthsValid))
    }
}