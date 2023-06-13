package com.latticeonfhir.android.ui.patienteditscreen.address

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.enums.ChangeTypeEnum
import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.model.patch.ChangeRequest
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.patient.PatientRepository
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.ui.patientregistration.step3.Address
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
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
        return true
    }

    suspend fun updateBasicInfo(patientResponse: PatientResponse): Int {
        val response = patientRepository.updatePatientData(patientResponse = patientResponse)
        if (response > 0) {
            if (checkIsEdit()) {
                if (patientResponse.fhirId != null) {
                    genericRepository.insertOrUpdatePatchEntity(
                        patientFhirId = patientResponse.fhirId,
                        map = mapOf(
                            Pair(
                                "permanentAddress", ChangeRequest(
                                    value = patientResponse.permanentAddress,
                                    operation = ChangeTypeEnum.REPLACE.value
                                )
                            )
                        ),
                        typeEnum = GenericTypeEnum.PATIENT
                    )

                } else {
                    genericRepository.insertOrUpdatePostEntity(
                        patientId = patientResponse.id,
                        entity = patientResponse,
                        typeEnum = GenericTypeEnum.PATIENT
                    )
                }
            }
        }
        return response
    }


}

