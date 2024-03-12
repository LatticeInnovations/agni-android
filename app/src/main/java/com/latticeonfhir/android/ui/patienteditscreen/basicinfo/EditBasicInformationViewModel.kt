package com.latticeonfhir.android.ui.patienteditscreen.basicinfo

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.viewModelScope
import com.google.android.fhir.FhirEngine
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.utils.constants.patient.ContactPointConstants.EMAIL
import com.latticeonfhir.android.utils.constants.patient.ContactPointConstants.PHONE
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.ageToPatientBirthDate
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.dateToDOB
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toDay
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toFullMonth
import com.latticeonfhir.android.utils.converters.responseconverter.TimeConverter.toYear
import com.latticeonfhir.android.utils.regex.OnlyNumberRegex
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.ContactPoint
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.StringType
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class EditBasicInformationViewModel @Inject constructor(
    private val fhirEngine: FhirEngine
) :
    BaseViewModel(), DefaultLifecycleObserver {
    var isLaunched by mutableStateOf(false)
    var isEditing by mutableStateOf(false)
    var isUpdating by mutableStateOf(false)
    var patient by mutableStateOf(Patient())

    val onlyNumbers = OnlyNumberRegex.onlyNumbers

    val maxFirstNameLength = 100
    val maxMiddleNameLength = 100
    val maxLastNameLength = 100
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
    var genderAtBirth by mutableStateOf("")

    //temp var
    private var firstNameTemp by mutableStateOf("")
    private var middleNameTemp by mutableStateOf("")
    private var lastNameTemp by mutableStateOf("")
    private var phoneNumberTemp by mutableStateOf("")
    private var emailTemp by mutableStateOf("")
    private var dobAgeSelectorTemp by mutableStateOf("dob")
    private var dobDayTemp by mutableStateOf("")
    private var dobMonthTemp by mutableStateOf("")
    private var dobYearTemp by mutableStateOf("")
    private var yearsTemp by mutableStateOf("")
    private var monthsTemp by mutableStateOf("")
    private var daysTemp by mutableStateOf("")
    private var genderTemp by mutableStateOf("")

    var monthsList = mutableStateListOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    var isNameValid by mutableStateOf(false)
    var isEmailValid by mutableStateOf(false)
    var isPhoneValid by mutableStateOf(false)
    var isDobDayValid by mutableStateOf(false)
    private var isDobMonthValid by mutableStateOf(false)
    var isDobYearValid by mutableStateOf(false)
    var isAgeDaysValid by mutableStateOf(false)
    var isAgeMonthsValid by mutableStateOf(false)
    var isAgeYearsValid by mutableStateOf(false)


    fun basicInfoValidation(): Boolean {
        if (firstName.length < 3 || firstName.length > 100)
            return false
        if (middleName.length > 100 || lastName.length > 100)
            return false
        if (dobAgeSelector == "dob" && (dobDay.isEmpty() || dobMonth.isEmpty() || dobYear.isEmpty()) || (isDobDayValid || isDobMonthValid || isDobYearValid))
            return false
        if (dobAgeSelector == "age" && (days.isEmpty() || months.isEmpty() || years.isEmpty()) || (isAgeDaysValid || isAgeMonthsValid || isAgeYearsValid))
            return false
        if (phoneNumber.length != 10)
            return false
        if (email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return false
        return genderAtBirth != ""
    }

    fun checkIsEdit(): Boolean {
        return firstName != firstNameTemp ||
                middleName != middleNameTemp ||
                lastName != lastNameTemp ||
                phoneNumber != phoneNumberTemp ||
                email != emailTemp ||
                dobAgeSelector != dobAgeSelectorTemp ||
                dobDay != dobDayTemp ||
                dobMonth != dobMonthTemp ||
                dobYear != dobYearTemp ||
                genderAtBirth != genderTemp
    }

    fun revertChanges(): Boolean {
        firstName = firstNameTemp
        middleName = middleNameTemp
        lastName = lastNameTemp
        phoneNumber = phoneNumberTemp
        email = emailTemp
        dobAgeSelector = dobAgeSelectorTemp
        dobDay = dobDayTemp
        dobMonth = dobMonthTemp
        dobYear = dobYearTemp
        days = ""
        months = ""
        years = ""
        genderAtBirth = genderTemp
        isNameValid = false
        isEmailValid = false
        isPhoneValid = false
        isDobDayValid = false
        isDobMonthValid = false
        isDobYearValid = false
        isAgeDaysValid = false
        isAgeMonthsValid = false
        isAgeYearsValid = false
        return true
    }


    fun updateBasicInfo(updated: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            fhirEngine.update(
                patient.apply {
                    name.clear()
                    name.add(
                        HumanName().apply {
                            given.add(
                                StringType(
                                    firstName.replaceFirstChar {
                                        it.titlecase(Locale.getDefault())
                                    })
                            )
                            given.add(
                                StringType(
                                    middleName.replaceFirstChar {
                                        it.titlecase(Locale.getDefault())
                                    }
                                )
                            )
                            family = lastName.replaceFirstChar {
                                it.titlecase(Locale.getDefault())
                            }
                        }
                    )
                    birthDate = if (dobAgeSelector == "dob")
                        "$dobDay $dobMonth $dobYear".dateToDOB()
                    else ageToPatientBirthDate(
                        years.toIntOrNull() ?: 0,
                        months.toIntOrNull() ?: 0,
                        days.toIntOrNull() ?: 0
                    )
                    telecom.clear()
                    telecom.add(
                        ContactPoint().apply {
                            system = ContactPoint.ContactPointSystem.PHONE
                            rank = 1
                            value = phoneNumber
                        }
                    )
                    if (email.isNotBlank()) {
                        telecom.add(
                            ContactPoint().apply {
                                system = ContactPoint.ContactPointSystem.EMAIL
                                value = email
                            }
                        )
                    }
                    gender = Enumerations.AdministrativeGender.fromCode(genderAtBirth)
                }
            )
            updated()
        }
    }
    
    internal fun setData() {
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

        //set temp value
        firstNameTemp = firstName
        middleNameTemp = middleName
        lastNameTemp = lastName
        phoneNumberTemp = phoneNumber
        emailTemp = email
        dobAgeSelectorTemp = dobAgeSelector
        dobDayTemp = dobDay
        dobMonthTemp = dobMonth
        dobYearTemp = dobYear
        daysTemp = days
        monthsTemp = months
        yearsTemp = years
        genderTemp = genderAtBirth
    }
}