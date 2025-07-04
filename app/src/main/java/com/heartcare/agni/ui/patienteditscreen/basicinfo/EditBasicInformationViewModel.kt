package com.heartcare.agni.ui.patienteditscreen.basicinfo

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.viewModelScope
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.local.enums.ChangeTypeEnum
import com.heartcare.agni.data.local.model.patch.ChangeRequest
import com.heartcare.agni.data.local.repository.generic.GenericRepository
import com.heartcare.agni.data.local.repository.patient.PatientRepository
import com.heartcare.agni.data.local.repository.vaccination.ImmunizationRecommendationRepository
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter
import com.heartcare.agni.utils.converters.responseconverter.TimeConverter.toMonthInteger
import com.heartcare.agni.utils.regex.AgeRegex
import com.heartcare.agni.utils.regex.DobRegex
import com.heartcare.agni.utils.regex.OnlyNumberRegex
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditBasicInformationViewModel @Inject constructor(
    val patientRepository: PatientRepository,
    val genericRepository: GenericRepository,
    val immunizationRecommendationRepository: ImmunizationRecommendationRepository
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

    var showDOBWarning by mutableStateOf(false)

    var monthsList = mutableStateListOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    var isNameValid by mutableStateOf(false)
    var isEmailValid by mutableStateOf(false)
    var isPhoneValid by mutableStateOf(false)
    var isAgeDaysValid by mutableStateOf(false)
    var isAgeMonthsValid by mutableStateOf(false)
    var isAgeYearsValid by mutableStateOf(false)


    fun basicInfoValidation(): Boolean {
        if (firstName.length < 3 || firstName.length > 100)
            return false
        if (middleName.length > 100 || lastName.length > 100)
            return false
        if (checkDob())
            return false
        if (dobAgeSelector == "age" && (days.isEmpty() && months.isEmpty() && years.isEmpty()) || (isAgeDaysValid || isAgeMonthsValid || isAgeYearsValid))
            return false
        if (isPhoneValid || phoneNumber.isBlank())
            return false
        if (email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return false
        return gender != ""
    }

    private fun checkDob(): Boolean {
        return dobAgeSelector == "dob" && ((dobDay.isBlank() || dobMonth.isBlank() || dobYear.isBlank()) || (!TimeConverter.isDOBValid(
            dobDay.toInt(),
            dobMonth.toMonthInteger(),
            dobYear.toInt()
        )))

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
                    saveFirstName(patientResponse)
                    checkIsValueChange(patientResponse, middleName, middleNameTemp, "middleName")
                    checkIsValueChange(patientResponse, lastName, lastNameTemp, "lastName")
                    checkIsValueChange(patientResponse, email, emailTemp, "email")

                    saveGender(patientResponse)
                    if (phoneNumber != phoneNumberTemp) {
                        genericRepository.insertOrUpdatePatientPatchEntity(
                            patientFhirId = patientResponse.fhirId,
                            map = mapOf(
                                Pair(
                                    "mobileNumber", ChangeRequest(
                                        value = patientResponse.mobileNumber,
                                        operation = ChangeTypeEnum.REPLACE.value
                                    )
                                )
                            )
                        )
                    }
                    if (patientResponse.birthDate != birthDate) {
                        genericRepository.insertOrUpdatePatientPatchEntity(
                            patientFhirId = patientResponse.fhirId,
                            map = mapOf(
                                Pair(
                                    "birthDate", ChangeRequest(
                                        value = patientResponse.birthDate,
                                        operation = ChangeTypeEnum.REPLACE.value
                                    )
                                )
                            )
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

    private suspend fun saveGender(patientResponse: PatientResponse) {
        if (patientResponse.gender != genderTemp) {
            genericRepository.insertOrUpdatePatientPatchEntity(
                patientFhirId = patientResponse.fhirId!!,
                map = mapOf(
                    Pair(
                        "gender", ChangeRequest(
                            value = patientResponse.gender,
                            operation = ChangeTypeEnum.REPLACE.value
                        )
                    )
                )
            )
        }

    }

    private suspend fun saveFirstName(patientResponse: PatientResponse) {
        if (firstName != firstNameTemp) {
            genericRepository.insertOrUpdatePatientPatchEntity(
                patientFhirId = patientResponse.fhirId!!,
                map = mapOf(
                    Pair(
                        "firstName", ChangeRequest(
                            value = firstName, operation = ChangeTypeEnum.REPLACE.value
                        )
                    )
                )
            )
        }
    }

    private suspend fun checkIsValueChange(
        patientResponse: PatientResponse,
        value: String,
        tempValue: String,
        key: String
    ) {
        when {
            value != tempValue && tempValue.isNotEmpty() && value.isNotEmpty() -> {
                genericRepository.insertOrUpdatePatientPatchEntity(
                    patientFhirId = patientResponse.fhirId!!,
                    map = mapOf(
                        Pair(
                            key, ChangeRequest(
                                value = value, operation = ChangeTypeEnum.REPLACE.value
                            )
                        )
                    )
                )
            }

            value != tempValue && tempValue.isNotEmpty() && value.isEmpty() -> {
                genericRepository.insertOrUpdatePatientPatchEntity(
                    patientFhirId = patientResponse.fhirId!!,
                    map = mapOf(
                        Pair(
                            key, ChangeRequest(
                                value = tempValue, operation = ChangeTypeEnum.REMOVE.value
                            )
                        )
                    )
                )

            }

            value != tempValue && tempValue.isEmpty() && value.isNotEmpty() -> {
                genericRepository.insertOrUpdatePatientPatchEntity(
                    patientFhirId = patientResponse.fhirId!!,
                    map = mapOf(
                        Pair(
                            key, ChangeRequest(
                                value = value, operation = ChangeTypeEnum.ADD.value
                            )
                        )
                    )
                )

            }
        }


    }

    internal fun clearOldImmunizationRecommendation(
        patientId: String,
        ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    ) {
        viewModelScope.launch(ioDispatcher) {
            immunizationRecommendationRepository.clearImmunizationRecommendationOfPatient(patientId)
        }
    }

}