package com.heartcare.agni.ui.patienteditscreen.identification

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.viewModelScope
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.local.repository.generic.GenericRepository
import com.heartcare.agni.data.local.repository.identifier.IdentifierRepository
import com.heartcare.agni.data.local.repository.patient.PatientRepository
import com.heartcare.agni.data.server.model.patient.PatientIdentifier
import com.heartcare.agni.data.server.model.patient.PatientResponse
import com.heartcare.agni.utils.constants.IdentificationConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditIdentificationViewModel @Inject constructor(
    private val patientRepository: PatientRepository,
    private val genericRepository: GenericRepository,
    private val identifierRepository: IdentifierRepository
) : BaseViewModel(), DefaultLifecycleObserver {
    var isLaunched by mutableStateOf(false)
    var isEditing by mutableStateOf(false)

    val maxHospitalIdLength = 6
    val maxNationalIdLength = 6

    var hospitalId by mutableStateOf("")
    var nationalId by mutableStateOf("")
    var nationalIdUse by mutableStateOf("")
    var isVerifyClicked by mutableStateOf(false)
    var isNationalIdVerified by mutableStateOf(false)
    var isHospitalIdValid by mutableStateOf(false)

    val identifierList = mutableListOf<PatientIdentifier>()
    var patient by mutableStateOf<PatientResponse?>(null)

    // temp
    var hospitalIdTemp by mutableStateOf("")
    var nationalIdTemp by mutableStateOf("")
    var nationalIdUseTemp by mutableStateOf("")
    var isNationalIdVerifiedTemp by mutableStateOf(false)

    fun identityInfoValidation(): Boolean {
        if (isHospitalIdValid)
            return false
        return isVerifyClicked || nationalId.isBlank()
    }

    fun checkIsEdit(): Boolean {
        return hospitalId != hospitalIdTemp ||
                nationalId != nationalIdTemp ||
                isNationalIdVerified != isNationalIdVerifiedTemp ||
                nationalIdUse != nationalIdUseTemp
    }

    fun revertChanges(): Boolean {
        hospitalId = hospitalIdTemp
        nationalId = nationalIdTemp
        isNationalIdVerified = isNationalIdVerifiedTemp
        nationalIdUse = nationalIdUseTemp
        isVerifyClicked = false
        return true
    }

    fun updateBasicInfo(patientResponse: PatientResponse) {
        viewModelScope.launch(Dispatchers.IO) {
            val toBeDeletedList = mutableListOf<PatientIdentifier>()
            if (hospitalIdTemp != hospitalId) {
                toBeDeletedList.add(
                    PatientIdentifier(
                        identifierType = IdentificationConstants.HOSPITAL_ID,
                        identifierNumber = hospitalIdTemp,
                        code = null,
                        use = null
                    )
                )
            }

            if (nationalIdTemp != nationalId) {
                toBeDeletedList.add(
                    PatientIdentifier(
                        identifierType = IdentificationConstants.NATIONAL_ID,
                        identifierNumber = nationalIdTemp,
                        code = null,
                        use = nationalIdUseTemp
                    )
                )
            }

            identifierRepository.deleteIdentifier(
                patientIdentifier = toBeDeletedList.toTypedArray(),
                patientId = patientResponse.id
            )

            val response = patientRepository.updatePatientData(patientResponse = patientResponse)
            if (response > 0) {
                identifierRepository.insertIdentifierList(patientResponse = patientResponse)
                if (patientResponse.fhirId != null) {
                    genericRepository.insertOrUpdatePatientPatchEntity(
                        patientFhirId = patientResponse.fhirId,
                        patientResponse = patientResponse
                    )
                } else {
                    genericRepository.insertPatient(
                        patientResponse
                    )
                }
            }
        }
    }
}