package com.latticeonfhir.android.ui.patienteditscreen.address

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.enums.ChangeTypeEnum
import com.latticeonfhir.android.data.local.model.patch.ChangeRequest
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.patient.PatientRepository
import com.latticeonfhir.android.data.local.repository.patient.lastupdated.PatientLastUpdatedRepository
import com.latticeonfhir.android.data.local.roomdb.entities.patient.PatientLastUpdatedEntity
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.ui.patientregistration.step3.Address
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class EditPatientAddressViewModel @Inject constructor(
    val patientRepository: PatientRepository,
    val genericRepository: GenericRepository,
    private val patientLastUpdatedRepository: PatientLastUpdatedRepository
) : BaseViewModel(), DefaultLifecycleObserver {
    var isLaunched by mutableStateOf(false)
    var isEditing by mutableStateOf(false)

    var homeAddress by mutableStateOf(Address())
    var homeAddressTemp by mutableStateOf(Address())
    var workAddress by mutableStateOf(Address())

    var addWorkAddress by mutableStateOf(false)

    fun addressInfoValidation(): Boolean {
        if (homeAddress.pincode.length < 6 || homeAddress.state.isBlank() || homeAddress.addressLine1.isBlank()
            || homeAddress.city.isBlank()
        )
            return false
        if (addWorkAddress && (workAddress.pincode.length < 6 || workAddress.state.isBlank() || workAddress.addressLine1.isBlank()
                    || workAddress.city.isBlank())
        )
            return false
        return true
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
                patientLastUpdatedRepository.insertPatientLastUpdatedData(
                    PatientLastUpdatedEntity(
                        patientId = patientResponse.id,
                        lastUpdated = Date()
                    )
                )
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
        if (value != tempValue && tempValue.isNotEmpty() && value.isNotEmpty()) {
            genericRepository.insertOrUpdatePatientPatchEntity(
                patientFhirId = patientResponse.fhirId!!,
                map = mapOf(
                    Pair(
                        "permanentAddress", ChangeRequest(
                            value = patientResponse.permanentAddress,
                            operation = ChangeTypeEnum.REPLACE.value
                        )
                    )
                )
            )
        } else if (value != tempValue && tempValue.isNotEmpty() && value.isEmpty()) {
            genericRepository.insertOrUpdatePatientPatchEntity(
                patientFhirId = patientResponse.fhirId!!,
                map = mapOf(
                    Pair(
                        "permanentAddress", ChangeRequest(
                            value = patientResponse.permanentAddress,
                            operation = ChangeTypeEnum.REPLACE.value
                        )
                    )
                )
            )

        } else if (value != tempValue && tempValue.isEmpty() && value.isNotEmpty()) {
            genericRepository.insertOrUpdatePatientPatchEntity(
                patientFhirId = patientResponse.fhirId!!,
                map = mapOf(
                    Pair(
                        "permanentAddress", ChangeRequest(
                            value = patientResponse.permanentAddress,
                            operation = ChangeTypeEnum.ADD.value
                        )
                    )
                )
            )

        }


    }


}

