package com.latticeonfhir.android.ui.patienteditscreen.address

import android.content.Context
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
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.ui.patientregistration.step3.Address
import com.latticeonfhir.android.utils.states.getBlockCode
import com.latticeonfhir.android.utils.states.getDistrictCode
import com.latticeonfhir.android.utils.states.getStateCode
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

    var homeAddress by mutableStateOf(Address())
    var homeAddressTemp by mutableStateOf(Address())

    fun addressInfoValidation(): Boolean {
        return !(
                homeAddress.state.isBlank() || homeAddress.isStateValid
                        || homeAddress.district.isBlank() || homeAddress.isDistrictValid
                        || homeAddress.isPostalCodeValid
                )
    }

    fun checkIsEdit(): Boolean {
        return homeAddress.pincode != homeAddressTemp.pincode ||
                homeAddress.state != homeAddressTemp.state ||
                homeAddress.addressLine1 != homeAddressTemp.addressLine1 ||
                homeAddress.addressLine2 != homeAddressTemp.addressLine2 ||
                homeAddress.city != homeAddressTemp.city ||
                homeAddress.district != homeAddressTemp.district ||
                homeAddress.block != homeAddressTemp.block
    }


    fun revertChanges(): Boolean {
        homeAddress.pincode = homeAddressTemp.pincode
        homeAddress.state = homeAddressTemp.state
        homeAddress.city = homeAddressTemp.city
        homeAddress.district = homeAddressTemp.district
        homeAddress.addressLine1 = homeAddressTemp.addressLine1
        homeAddress.addressLine2 = homeAddressTemp.addressLine2
        homeAddress.block = homeAddressTemp.block
        homeAddress.isPostalCodeValid = false
        homeAddress.isAddressLine1Valid = false
        homeAddress.isCityValid = false
        homeAddress.isStateValid = false
        homeAddress.isBlockValid = false
        return true
    }

    fun updateBasicInfo(
        context: Context,
        patientResponse: PatientResponse
    ) {
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
                        homeAddress.city,
                        homeAddressTemp.city
                    )
                    checkIsValueChange(
                        patientResponse,
                        listOf(
                            getStateCode(context, homeAddress.state),
                            homeAddress.state
                        ).joinToString("|"),
                        listOf(
                            getStateCode(context, homeAddressTemp.state),
                            homeAddressTemp.state
                        ).joinToString("|")
                    )
                    checkIsValueChange(
                        patientResponse,
                        listOf(
                            getDistrictCode(
                                context = context,
                                stateName = homeAddress.state,
                                districtName = homeAddress.district
                            ),
                            homeAddress.district
                        ).joinToString("|"),
                        listOf(
                            getDistrictCode(
                                context = context,
                                stateName = homeAddressTemp.state,
                                districtName = homeAddressTemp.district
                            ),
                            homeAddressTemp.district
                        ).joinToString("|")
                    )
                    checkIsValueChange(
                        patientResponse,
                        if (homeAddress.block.isBlank()) ""
                        else {
                            listOf(
                                getBlockCode(
                                    context = context,
                                    stateName = homeAddress.state,
                                    districtName = homeAddress.district,
                                    blockName = homeAddress.block
                                ),
                                homeAddress.block
                            ).joinToString("|")
                        },

                        if (homeAddressTemp.block.isBlank()) ""
                        else {
                            listOf(
                                getBlockCode(
                                    context = context,
                                    stateName = homeAddressTemp.state,
                                    districtName = homeAddressTemp.district,
                                    blockName = homeAddressTemp.block
                                ),
                                homeAddressTemp.block
                            ).joinToString("|")
                        }
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

