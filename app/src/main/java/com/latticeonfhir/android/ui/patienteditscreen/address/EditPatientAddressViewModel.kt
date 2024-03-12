package com.latticeonfhir.android.ui.patienteditscreen.address

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.viewModelScope
import com.google.android.fhir.FhirEngine
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.ui.patientregistration.step3.Address
import com.latticeonfhir.android.utils.constants.patient.AddressConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.StringType
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class EditPatientAddressViewModel @Inject constructor(
    private val fhirEngine: FhirEngine
) : BaseViewModel(), DefaultLifecycleObserver {
    var isLaunched by mutableStateOf(false)
    var isEditing by mutableStateOf(false)
    var isUpdating by mutableStateOf(false)
    var patient by mutableStateOf(Patient())

    var homeAddress by mutableStateOf(Address())
    var homeAddressTemp by mutableStateOf(Address())
    var workAddress by mutableStateOf(Address())

    var addWorkAddress by mutableStateOf(false)

    fun addressInfoValidation(): Boolean {
        if (homeAddress.pincode.length < 6 || homeAddress.state.isBlank() || homeAddress.addressLine1.isBlank()
            || homeAddress.city.isBlank()
        )
            return false
        return !(addWorkAddress && (workAddress.pincode.length < 6 || workAddress.state.isBlank() || workAddress.addressLine1.isBlank()
                || workAddress.city.isBlank()))
    }

    fun checkIsEdit(): Boolean {
        return homeAddress.pincode != homeAddressTemp.pincode ||
                homeAddress.state != homeAddressTemp.state ||
                homeAddress.addressLine1 != homeAddressTemp.addressLine1 ||
                homeAddress.addressLine2 != homeAddressTemp.addressLine2 ||
                homeAddress.city != homeAddressTemp.city ||
                homeAddress.district != homeAddressTemp.district
    }


    fun revertChanges(): Boolean {
        homeAddress.pincode = homeAddressTemp.pincode
        homeAddress.state = homeAddressTemp.state
        homeAddress.city = homeAddressTemp.city
        homeAddress.district = homeAddressTemp.district
        homeAddress.addressLine1 = homeAddressTemp.addressLine1
        homeAddress.addressLine2 = homeAddressTemp.addressLine2
        homeAddress.isPostalCodeValid = false
        homeAddress.isAddressLine1Valid = false
        homeAddress.isCityValid = false
        homeAddress.isStateValid = false
        return true
    }

    fun updateAddressInfo(updated: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            fhirEngine.update(
                patient.apply {
                    address.clear()
                    address.add(
                        org.hl7.fhir.r4.model.Address().apply {
                            use = org.hl7.fhir.r4.model.Address.AddressUse.HOME
                            postalCode = homeAddress.pincode
                            district = homeAddress.district.replaceFirstChar {
                                it.titlecase(Locale.getDefault())
                            }
                            state = homeAddress.state
                            city = homeAddress.city.replaceFirstChar {
                                it.titlecase(Locale.getDefault())
                            }
                            line.add(
                                StringType(
                                    homeAddress.addressLine1.replaceFirstChar {
                                        it.titlecase(Locale.getDefault())
                                    }
                                )
                            )
                            line.add(
                                StringType(
                                    homeAddress.addressLine2.replaceFirstChar {
                                        it.titlecase(Locale.getDefault())
                                    }
                                )
                            )
                            text = "${homeAddress.addressLine1} ${homeAddress.addressLine2}"
                            country = "India"
                        }
                    )
                }
            )
            updated()
        }
    }


    internal fun setData() {
        patient.run {
            address.forEach { a ->
                if (a.use.toCode() == AddressConstants.HOME) {
                    homeAddress.pincode = a.postalCode
                    homeAddress.state = a.state
                    homeAddress.city = a.city
                    homeAddress.district = a.district ?: ""
                    homeAddress.addressLine1 = a.line[0].value
                    if (a.line.size > 1) homeAddress.addressLine2 = a.line[1].value
                }
            }

            homeAddressTemp.pincode = homeAddress.pincode
            homeAddressTemp.state = homeAddress.state
            homeAddressTemp.city = homeAddress.city
            homeAddressTemp.district = homeAddress.district
            homeAddressTemp.addressLine1 = homeAddress.addressLine1
            homeAddressTemp.addressLine2 = homeAddress.addressLine2
        }
    }
}

