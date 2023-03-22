package com.latticeonfhir.android.ui.main.patientregistration.step3

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import com.latticeonfhir.android.base.viewmodel.BaseViewModel

class PatientRegistrationStepThreeViewModel: BaseViewModel(), DefaultLifecycleObserver {
    var isLaunched by mutableStateOf(false)

    var homeAddress by mutableStateOf(Address())
    var workAddress by mutableStateOf(Address())

    var addWorkAddress by mutableStateOf(false)

    val statesList = listOf("Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar",
        "Chhattisgarh", "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jharkhand",
        "Karnataka", "Kerala", "Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya",
        "Mizoram", "Nagaland", "Odisha", "Punjab", "Rajasthan", "Sikkim", "Tamil Nadu",
        "Telangana", "Tripura", "Uttar Pradesh", "Uttarakhand", "West Bengal")

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
    var state by mutableStateOf("")
    var area by mutableStateOf("")
    var town by mutableStateOf("")
    var city by mutableStateOf("")
    var isPostalCodeValid by mutableStateOf(false)
    var isStateValid by mutableStateOf(false)
    var isAreaValid by mutableStateOf(false)
    var isTownValid by mutableStateOf(false)
    var isCityValid by mutableStateOf(false)
}