package com.heartcare.agni.ui.patienteditscreen.basicinfo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.viewModelScope
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.local.repository.generic.GenericRepository
import com.heartcare.agni.data.local.repository.patient.PatientRepository
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toMonthInteger
import com.heartcare.agni.utils.regex.AgeRegex
import com.heartcare.agni.utils.regex.DobRegex
import com.heartcare.agni.utils.regex.OnlyNumberRegex
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditBasicInformationViewModel @Inject constructor(
    val patientRepository: PatientRepository,
    val genericRepository: GenericRepository
) : BaseViewModel(), DefaultLifecycleObserver {
    var isLaunched by mutableStateOf(false)

    val onlyNumbers = OnlyNumberRegex.onlyNumbers
    val ageRegex = AgeRegex.ageRegex
    val dobRegex = DobRegex.dobRegex

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

    //temp var
    var firstNameTemp by mutableStateOf("")
    var lastNameTemp by mutableStateOf("")
    var phoneNumberTemp by mutableStateOf("")
    var emailTemp by mutableStateOf("")
    var dobAgeSelectorTemp by mutableStateOf("dob")
    var dobDayTemp by mutableStateOf("")
    var dobMonthTemp by mutableStateOf("")
    var dobYearTemp by mutableStateOf("")
    var yearsTemp by mutableStateOf("")
    var monthsTemp by mutableStateOf("")
    var daysTemp by mutableStateOf("")
    var genderTemp by mutableStateOf("")
    var birthDate by mutableStateOf("")
    var motherNameTemp by mutableStateOf("")
    var fatherNameTemp by mutableStateOf("")
    var spouseNameTemp by mutableStateOf("")
    var isPersonDeceasedTemp by mutableIntStateOf(0)
    var selectedDeceasedReasonTemp by mutableStateOf("")

    var monthsList = mutableStateListOf(
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

    fun basicInfoValidation(): Boolean {
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

    fun splitDOB(dob: String): Triple<Int, String, Int> {
        val dobParts = dob.trim().split("-")
        val year = dobParts[0].toInt()
        val month = dobParts[1].toInt()
        val day = dobParts[2].toInt()
        return Triple(day, monthsList[month - 1], year)
    }

    fun splitAge(dob: String): Triple<Int, Int, Int> {
        val dobParts = dob.trim().split("-")
        val year = dobParts[0].toInt()
        val month = dobParts[1].toInt()
        val day = dobParts[2].toInt()

        return Triple(day, month, year)
    }

    fun checkIsEdit(): Boolean {
        return firstName != firstNameTemp ||
                lastName != lastNameTemp ||
                phoneNumber != phoneNumberTemp ||
                email != emailTemp ||
                dobAgeSelector != dobAgeSelectorTemp ||
                dobDay != dobDayTemp ||
                dobMonth != dobMonthTemp ||
                dobYear != dobYearTemp ||
                gender != genderTemp ||
                motherName != motherNameTemp ||
                fatherName != fatherNameTemp ||
                spouseName != spouseNameTemp ||
                isPersonDeceased != isPersonDeceasedTemp ||
                selectedDeceasedReason != selectedDeceasedReasonTemp
    }

    fun revertChanges(): Boolean {
        firstName = firstNameTemp
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
        gender = genderTemp
        motherName = motherNameTemp
        fatherName = fatherNameTemp
        spouseName = spouseNameTemp
        isPersonDeceased = isPersonDeceasedTemp
        selectedDeceasedReason = selectedDeceasedReasonTemp
        isFirstNameValid = false
        isLastNameValid = false
        isMotherNameValid = false
        isPhoneValid = false
        isAgeDaysValid = false
        isAgeMonthsValid = false
        isAgeYearsValid = false
        return true
    }


    fun updateBasicInfo(patientResponse: PatientResponse) {

        viewModelScope.launch(Dispatchers.IO) {

            val response = patientRepository.updatePatientData(patientResponse = patientResponse)
            if (response > 0) {
                if (patientResponse.fhirId != null) {
                    genericRepository.insertOrUpdatePatientPatchEntity(
                        patientFhirId = patientResponse.fhirId,
                        patientResponse = patientResponse
                    )
                } else {
                    genericRepository.insertPatient(
                        patientResponse
                    )
                }
            }
        }
    }
}