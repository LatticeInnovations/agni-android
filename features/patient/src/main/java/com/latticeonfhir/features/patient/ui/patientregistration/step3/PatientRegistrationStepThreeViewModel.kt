package com.latticeonfhir.features.patient.ui.patientregistration.step3

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import com.latticeonfhir.android.base.viewmodel.BaseViewModel

class PatientRegistrationStepThreeViewModel : BaseViewModel(), DefaultLifecycleObserver {
    var isLaunched by mutableStateOf(false)
    var checkedState by mutableStateOf(false)

    var homeAddress by mutableStateOf(Address())

    var workAddress by mutableStateOf(Address())

    var addWorkAddress by mutableStateOf(false)

    fun addressInfoValidation(): Boolean {
        if (homeAddress.pincode.length < 6 || homeAddress.state == "" || homeAddress.addressLine1 == ""
            || homeAddress.city == ""
        )
            return false
        if (addWorkAddress && (workAddress.pincode.length < 6 || workAddress.state == "" || workAddress.addressLine1 == ""
                    || workAddress.city == "")
        )
            return false
        return true
    }
}

class Address {
    var pincode by mutableStateOf("")
    var state by mutableStateOf("")
    var addressLine1 by mutableStateOf("")
    var addressLine2 by mutableStateOf("")
    var city by mutableStateOf("")
    var district by mutableStateOf("")
    var isPostalCodeValid by mutableStateOf(false)
    var isAddressLine1Valid by mutableStateOf(false)
    var isCityValid by mutableStateOf(false)
    var isStateValid by mutableStateOf(false)
}