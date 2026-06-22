package com.latticeonfhir.android.ui.patientregistration.step3

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
        return !(
                homeAddress.state.isBlank() || homeAddress.isStateValid
                        || homeAddress.district.isBlank() || homeAddress.isDistrictValid
                )
    }
}

class Address {
    var pincode by mutableStateOf("")
    var isPostalCodeValid by mutableStateOf(false)

    var state by mutableStateOf("")
    var isStateValid by mutableStateOf(false)

    var district by mutableStateOf("")
    var isDistrictValid by mutableStateOf(false)

    var block by mutableStateOf("")
    var isBlockValid by mutableStateOf(false)

    var city by mutableStateOf("")
    var isCityValid by mutableStateOf(false)

    var addressLine1 by mutableStateOf("")
    var isAddressLine1Valid by mutableStateOf(false)

    var addressLine2 by mutableStateOf("")
}