package com.latticeonfhir.core.ui.patienteditscreen.address

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.core.data.local.enums.ChangeTypeEnum
import com.latticeonfhir.android.data.local.model.patch.ChangeRequest
import com.latticeonfhir.core.data.local.repository.generic.GenericRepository
import com.latticeonfhir.core.data.local.repository.patient.PatientRepository
import com.latticeonfhir.core.data.server.model.patient.PatientResponse
import com.latticeonfhir.core.ui.patientregistration.step3.Address
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditPatientAddressViewModel @Inject constructor(
    val patientRepository: PatientRepository,
    val genericRepository: GenericRepository
) : BaseViewModel(), DefaultLifecycleObserver {
    var isLaunched by mutableStateOf(false)
    var isEditing by mutableStateOf(false)

    var homeAddress by mutableStateOf(Address())
    var homeAddressTemp by mutableStateOf(Address())
    private var workAddress by mutableStateOf(Address())

    private var addWorkAddress by mutableStateOf(false)

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

    fun updateBasicInfo(patientResponse: PatientResponse) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = patientRepository.updatePatientData(patientResponse = patientResponse)
            if (checkIsEdit() && response > 0) {
                if (patientResponse.fhirId != null) {
                    checkIsValueChange(
                        patientResponse,
                        homeAddress.pincode,
                        homeAddressTemp.pincode
                    )
                    checkIsValueChange(
                        patientResponse,
                        homeAddress.addressLine1,
                        homeAddressTemp.addressLine1
                    )
                    checkIsValueChange(
                        patientResponse,
                        homeAddress.addressLine2,
                        homeAddressTemp.addressLine2
                    )
                    checkIsValueChange(
                        patientResponse,
                        homeAddress.state,
                        homeAddressTemp.state
                    )
                    checkIsValueChange(
                        patientResponse,
                        homeAddress.city,
                        homeAddressTemp.city
                    )
                    checkIsValueChange(
                        patientResponse,
                        homeAddress.district,
                        homeAddressTemp.district
                    )

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
        tempValue: String
    ) {
        when {
            value != tempValue && tempValue.isNotEmpty() && value.isNotEmpty() -> {
                updateAddress(patientResponse, ChangeTypeEnum.REPLACE.value)
            }

            value != tempValue && tempValue.isNotEmpty() && value.isEmpty() -> {
                updateAddress(patientResponse, ChangeTypeEnum.REPLACE.value)

            }

            value != tempValue && tempValue.isEmpty() && value.isNotEmpty() -> {
                updateAddress(patientResponse, ChangeTypeEnum.ADD.value)
            }
        }


    }

    private suspend fun updateAddress(patientResponse: PatientResponse, operation: String) {
        genericRepository.insertOrUpdatePatientPatchEntity(
            patientFhirId = patientResponse.fhirId!!,
            map = mapOf(
                Pair(
                    "permanentAddress", ChangeRequest(
                        value = patientResponse.permanentAddress,
                        operation = operation
                    )
                )
            )
        )
    }


}

