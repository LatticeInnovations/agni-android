package com.latticeonfhir.android.ui.patienteditscreen.basicinfo

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.enums.ChangeTypeEnum
import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.model.patch.ChangeRequest
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.patient.PatientRepository
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.utils.regex.AgeRegex
import com.latticeonfhir.android.utils.regex.DobRegex
import com.latticeonfhir.android.utils.regex.OnlyNumberRegex
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditBasicInformationViewModel @Inject constructor(
    val patientRepository: PatientRepository,
    val genericRepository: GenericRepository
) :
    BaseViewModel(), DefaultLifecycleObserver {
    var isLaunched by mutableStateOf(false)
    var isEditing by mutableStateOf(false)

    val onlyNumbers = OnlyNumberRegex.onlyNumbers
    val ageRegex = AgeRegex.ageRegex
    val dobRegex = DobRegex.dobRegex


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
    var gender by mutableStateOf("")

    //temp var
    var firstNameTemp by mutableStateOf("")
    var middleNameTemp by mutableStateOf("")
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

    val daysList = (1..31).toList()
    var monthsList = mutableStateListOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    fun getMonthsList() {
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
        if (gender == "")
            return false
        return true
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
                middleName != middleNameTemp ||
                lastName != lastNameTemp ||
                phoneNumber != phoneNumberTemp ||
                email != emailTemp ||
                dobAgeSelector != dobAgeSelectorTemp ||
                dobDay != dobDayTemp ||
                dobMonth != dobMonthTemp ||
                dobYear != dobYearTemp ||
                gender != genderTemp
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
        gender = genderTemp
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


    fun updateBasicInfo(patientResponse: PatientResponse) {

        viewModelScope.launch(Dispatchers.IO) {

            val response = patientRepository.updatePatientData(patientResponse = patientResponse)
            if (response > 0) {
                if (patientResponse.fhirId != null) {
                    if (firstName != firstNameTemp) {
                        genericRepository.insertOrUpdatePatchEntity(
                            patientFhirId = patientResponse.fhirId,
                            map = mapOf(
                                Pair(
                                    "firstName", ChangeRequest(
                                        value = firstName, operation = ChangeTypeEnum.REPLACE.value
                                    )
                                )
                            ),
                            typeEnum = GenericTypeEnum.PATIENT
                        )
                    }
                    checkIsValueChange(patientResponse, middleName, middleNameTemp, "middleName")
                    checkIsValueChange(patientResponse, lastName, lastNameTemp, "lastName")
                    checkIsValueChange(patientResponse, email, emailTemp, "email")

                    if (patientResponse.gender != genderTemp) {
                        genericRepository.insertOrUpdatePatchEntity(
                            patientFhirId = patientResponse.fhirId,
                            map = mapOf(
                                Pair(
                                    "gender", ChangeRequest(
                                        value = patientResponse.gender,
                                        operation = ChangeTypeEnum.REPLACE.value
                                    )
                                )
                            ),
                            typeEnum = GenericTypeEnum.PATIENT
                        )
                    }
                    if (phoneNumber != phoneNumberTemp) {
                        genericRepository.insertOrUpdatePatchEntity(
                            patientFhirId = patientResponse.fhirId,
                            map = mapOf(
                                Pair(
                                    "mobileNumber", ChangeRequest(
                                        value = patientResponse.mobileNumber,
                                        operation = ChangeTypeEnum.REPLACE.value
                                    )
                                )
                            ),
                            typeEnum = GenericTypeEnum.PATIENT
                        )
                    }
                    if (patientResponse.birthDate != birthDate) {
                        genericRepository.insertOrUpdatePatchEntity(
                            patientFhirId = patientResponse.fhirId,
                            map = mapOf(
                                Pair(
                                    "birthDate", ChangeRequest(
                                        value = patientResponse.birthDate,
                                        operation = ChangeTypeEnum.REPLACE.value
                                    )
                                )
                            ),
                            typeEnum = GenericTypeEnum.PATIENT
                        )
                    }

                } else {
                    genericRepository.insertPatient(
                        patientResponse
                    )
                }
            }
        }
    }

    private suspend fun checkIsValueChange(
        patientResponse: PatientResponse,
        value: String,
        tempValue: String,
        key: String
    ) {
        if (value != tempValue && tempValue.isNotEmpty() && value.isNotEmpty()) {
            genericRepository.insertOrUpdatePatchEntity(
                patientFhirId = patientResponse.fhirId!!,
                map = mapOf(
                    Pair(
                        key, ChangeRequest(
                            value = value, operation = ChangeTypeEnum.REPLACE.value
                        )
                    )
                ),
                typeEnum = GenericTypeEnum.PATIENT
            )
        } else if (value != tempValue && tempValue.isNotEmpty() && value.isEmpty()) {
            genericRepository.insertOrUpdatePatchEntity(
                patientFhirId = patientResponse.fhirId!!,
                map = mapOf(
                    Pair(
                        key, ChangeRequest(
                            value = tempValue, operation = ChangeTypeEnum.REMOVE.value
                        )
                    )
                ),
                typeEnum = GenericTypeEnum.PATIENT
            )

        } else if (value != tempValue && tempValue.isEmpty() && value.isNotEmpty()) {
            genericRepository.insertOrUpdatePatchEntity(
                patientFhirId = patientResponse.fhirId!!,
                map = mapOf(
                    Pair(
                        key, ChangeRequest(
                            value = value, operation = ChangeTypeEnum.ADD.value
                        )
                    )
                ),
                typeEnum = GenericTypeEnum.PATIENT
            )

        }


    }

}