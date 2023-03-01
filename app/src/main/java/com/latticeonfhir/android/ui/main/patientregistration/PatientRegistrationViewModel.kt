package com.latticeonfhir.android.ui.main.patientregistration

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class PatientRegistrationViewModel: ViewModel() {
    val maxFirstNameLength = 150
    val maxMiddleNameLength = 150
    val maxLastNameLength = 150
    val maxEmailLength = 150
    val maxPassportIdLength = 8
    val maxVoterIdLength = 10
    val maxPatientIdLength = 10
    var step by mutableStateOf(1)
    var firstName by mutableStateOf("")
    var middleName by mutableStateOf("")
    var lastName by mutableStateOf("")
    var phoneNumber by mutableStateOf("")
    var email by mutableStateOf("")
    var dobAgeSelector by mutableStateOf("dob")
    var dob by mutableStateOf("")
    var years by mutableStateOf("")
    var months by mutableStateOf("")
    var days by mutableStateOf("")
    var gender by mutableStateOf("")
    var isPassportSelected by mutableStateOf(true)
    var isVoterSelected by mutableStateOf(false)
    var isPatientSelected by mutableStateOf(false)
    var passportId by mutableStateOf("")
    var voterId by mutableStateOf("")
    var patientId by mutableStateOf("")

    var homeAddress by mutableStateOf(Address())
    var workAddress by mutableStateOf(Address())

    var addWorkAddress by mutableStateOf(false)

    fun basicInfoValidation(): Boolean{
        if (firstName.length < 3 || firstName.length > 150)
            return false
        if (middleName.length > 150 || lastName.length > 150)
            return false
        if (dobAgeSelector == "dob" && dob == "")
            return false
        if (dobAgeSelector == "age" && (years == "" || months == "" || days == ""))
            return false
        if (phoneNumber.length < 10)
            return false
        if (email == "")
            return false
        if (gender == "")
            return false
        return true
    }

    fun identityInfoValidation(): Boolean{
        if (isPassportSelected == false && isVoterSelected == false && isPatientSelected == false)
            return false
        if (isPassportSelected && passportId.length<8)
            return false
        if (isVoterSelected && voterId.length<10)
            return false
        if(isPatientSelected && patientId.length<10)
            return false
        return true
    }

    fun addressInfoValidation(): Boolean{
        if (homeAddress.pincode.length < 6 || homeAddress.state=="" || homeAddress.area == ""
            || homeAddress.town == "" || homeAddress.city == "")
            return false
        if (addWorkAddress && (workAddress.pincode.length < 6 || workAddress.state=="" || workAddress.area == ""
                    || workAddress.town == "" || workAddress.city == ""))
            return false
        return true
    }
}

class Address {
    var pincode by mutableStateOf("")
    var state by mutableStateOf("Uttarakhand")
    var area by mutableStateOf("")
    var town by mutableStateOf("")
    var city by mutableStateOf("")
}