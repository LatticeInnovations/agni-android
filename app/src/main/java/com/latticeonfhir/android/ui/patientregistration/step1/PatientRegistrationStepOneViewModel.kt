package com.latticeonfhir.android.ui.patientregistration.step1

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.utils.constants.patient.ContactPointConstants.EMAIL
import com.latticeonfhir.android.utils.constants.patient.ContactPointConstants.PHONE
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toDay
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toFullMonth
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toYear
import org.hl7.fhir.r4.model.Patient

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
    internal var genderAtBirth by mutableStateOf("")

    internal var monthsList = mutableStateListOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    internal var isNameValid by mutableStateOf(false)
    internal var isEmailValid by mutableStateOf(false)
    internal var isPhoneValid by mutableStateOf(false)
    internal var isDobDayValid by mutableStateOf(false)
    private var isDobMonthValid by mutableStateOf(false)
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
        return genderAtBirth != ""
    }

    internal fun setData(patient: Patient) {
        patient.run {
            firstName = nameFirstRep.given[0].value
            middleName = if (nameFirstRep.given.size > 1) nameFirstRep.given[1].value else ""
            lastName = nameFirstRep.family ?: ""
            telecom.forEach { contactPoint ->
                when (contactPoint.system.toCode()) {
                    PHONE -> phoneNumber = contactPoint.value
                    EMAIL -> email = contactPoint.value
                }
            }
            dobAgeSelector = "dob"
            dobDay = birthDate.toDay()
            dobMonth = birthDate.toFullMonth()
            dobYear = birthDate.toYear()
            genderAtBirth = gender.toCode()
        }
    }
}