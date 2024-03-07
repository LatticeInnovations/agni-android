package com.latticeonfhir.android.ui.patientregistration.step3

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.utils.constants.patient.AddressConstants.HOME
import org.hl7.fhir.r4.model.Patient

class PatientRegistrationStepThreeViewModel : BaseViewModel(), DefaultLifecycleObserver {
    var isLaunched by mutableStateOf(false)

    var homeAddress by mutableStateOf(Address())

    fun addressInfoValidation(): Boolean {
        return !(homeAddress.pincode.length < 6 || homeAddress.state == "" || homeAddress.addressLine1 == ""
                || homeAddress.city == "")
    }

    internal fun setData(patient: Patient) {
        patient.run {
            address.forEach { a ->
                if (a.use.toCode() == HOME) {
                    homeAddress.pincode = a.postalCode
                    homeAddress.state = a.state
                    homeAddress.city = a.city
                    homeAddress.district = a.district ?: ""
                    homeAddress.addressLine1 = a.line[0].value
                    if (a.line.size > 1) homeAddress.addressLine2 = a.line[1].value
                }
            }
        }
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