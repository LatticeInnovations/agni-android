package com.latticeonfhir.android.ui.patienteditscreen.identification

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.enums.ChangeTypeEnum
import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.model.patch.ChangeRequest
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.identifier.IdentifierRepository
import com.latticeonfhir.android.data.local.repository.patient.PatientRepository
import com.latticeonfhir.android.data.local.roomdb.entities.patient.IdentifierEntity
import com.latticeonfhir.android.data.server.model.patient.PatientIdentifier
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import com.latticeonfhir.android.utils.constants.IdentificationConstants
import com.latticeonfhir.android.utils.converters.responseconverter.GsonConverters.toJson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EditIdentificationViewModel @Inject constructor(
    private val patientRepository: PatientRepository,
    private val genericRepository: GenericRepository,
    private val identifierRepository: IdentifierRepository
) : BaseViewModel(), DefaultLifecycleObserver {
    var isLaunched by mutableStateOf(false)
    var isEditing by mutableStateOf(false)

    val maxPassportIdLength = 8
    val maxVoterIdLength = 10
    val maxPatientIdLength = 10

    var isPassportSelected by mutableStateOf(true)
    var isVoterSelected by mutableStateOf(false)
    var isPatientSelected by mutableStateOf(false)
    var passportId by mutableStateOf("")
    var voterId by mutableStateOf("")
    var patientId by mutableStateOf("")

    val passportPattern = Regex("^[A-PR-WYa-pr-wy][1-9]\\d\\s?\\d{4}[1-9]$")
    val voterPattern = Regex("^[A-Za-z]{3}[0-9]{7}$")
    var isPassportValid by mutableStateOf(false)
    var isVoterValid by mutableStateOf(false)
    var isPatientValid by mutableStateOf(false)

    val identifierList = mutableListOf<PatientIdentifier>()
    var patient by mutableStateOf<PatientResponse?>(null)


    // temp
    var isPassportSelectedTemp by mutableStateOf(false)
    var isVoterSelectedTemp by mutableStateOf(false)
    var isPatientSelectedTemp by mutableStateOf(false)
    var passportIdTemp by mutableStateOf("")
    var voterIdTemp by mutableStateOf("")
    var patientIdTemp by mutableStateOf("")


    fun identityInfoValidation(): Boolean {
        if (!isPassportSelected && !isVoterSelected && !isPatientSelected)
            return false
        if (isPassportSelected && !passportPattern.matches(passportId))
            return false
        if (isVoterSelected && !voterPattern.matches(voterId))
            return false
        if (isPatientSelected && patientId.length < 10)
            return false
        return true
    }

    fun checkIsEdit(): Boolean {
        return isPassportSelected != isPassportSelectedTemp ||
                isVoterSelected != isVoterSelectedTemp ||
                isPatientSelected != isPatientSelectedTemp ||
                passportId != passportIdTemp ||
                voterId != voterIdTemp ||
                patientId != patientIdTemp
    }

    fun revertChanges(): Boolean {
        isPassportSelected = isPassportSelectedTemp
        isVoterSelected = isVoterSelectedTemp
        isPatientSelected = isPatientSelectedTemp
        passportId = passportIdTemp
        voterId = voterIdTemp
        patientId = patientIdTemp
        return true
    }

     fun updateBasicInfo(patientResponse: PatientResponse):Unit {
         viewModelScope.launch(Dispatchers.IO) {
             val toBeDeletedList = mutableListOf<PatientIdentifier>()
             if (passportIdTemp != passportId || !isPassportSelected) {
                 toBeDeletedList.add(
                     PatientIdentifier(
                         identifierType = IdentificationConstants.PASSPORT_TYPE,
                         identifierNumber = passportIdTemp,
                         code = null
                     )
                 )
             }

             if (voterIdTemp != voterId || !isPassportSelected) {
                 toBeDeletedList.add(
                     PatientIdentifier(
                         identifierType = IdentificationConstants.VOTER_ID_TYPE,
                         identifierNumber = voterIdTemp,
                         code = null
                     )
                 )
             }
             if (patientIdTemp != patientId || !isPassportSelected) {
                 toBeDeletedList.add(
                     PatientIdentifier(
                         identifierType = IdentificationConstants.PATIENT_ID_TYPE,
                         identifierNumber = patientIdTemp,
                         code = null
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
                     val list = mutableListOf<ChangeRequest>()

                     if (passportId != passportIdTemp && passportId.isEmpty() && passportIdTemp.isNotEmpty()) {


                         list.add(
                             ChangeRequest(
                                 value = PatientIdentifier(
                                     identifierType = IdentificationConstants.PASSPORT_TYPE,
                                     identifierNumber = passportIdTemp,
                                     code = null
                                 ), operation = ChangeTypeEnum.REMOVE.value,
                                 key = IdentificationConstants.PASSPORT_TYPE
                             )

                         )

                     } else if (passportId != passportIdTemp && passportIdTemp.isNotEmpty() && passportId.isNotEmpty()) {
                         list.add(
                             ChangeRequest(
                                 value = PatientIdentifier(
                                     identifierType = IdentificationConstants.PASSPORT_TYPE,
                                     identifierNumber = passportId,
                                     code = null
                                 ), operation = ChangeTypeEnum.REPLACE.value,
                                 key = IdentificationConstants.PASSPORT_TYPE
                             )

                         )

                     } else if (passportId != passportIdTemp && passportIdTemp.isEmpty() && passportId.isNotEmpty()) {
                         list.add(
                             ChangeRequest(
                                 value = PatientIdentifier(
                                     identifierType = IdentificationConstants.PASSPORT_TYPE,
                                     identifierNumber = passportId,
                                     code = null
                                 ), operation = ChangeTypeEnum.ADD.value,
                                 key = IdentificationConstants.PASSPORT_TYPE
                             )

                         )

                     }

                     if (voterId != voterIdTemp && voterId.isEmpty() && voterIdTemp.isNotEmpty()) {
                         list.add(
                             ChangeRequest(
                                 value = PatientIdentifier(
                                     identifierType = IdentificationConstants.VOTER_ID_TYPE,
                                     identifierNumber = voterIdTemp,
                                     code = null
                                 ), operation = ChangeTypeEnum.REMOVE.value,
                                 key = IdentificationConstants.VOTER_ID_TYPE
                             )

                         )

                     } else if (voterId != voterIdTemp && voterIdTemp.isNotEmpty() && voterId.isNotEmpty()) {
                         list.add(
                             ChangeRequest(
                                 value = PatientIdentifier(
                                     identifierType = IdentificationConstants.VOTER_ID_TYPE,
                                     identifierNumber = voterId,
                                     code = null
                                 ), operation = ChangeTypeEnum.REPLACE.value,
                                 key = IdentificationConstants.VOTER_ID_TYPE
                             )
                         )

                     } else if (voterId != voterIdTemp && voterIdTemp.isEmpty() && voterId.isNotEmpty()) {
                         list.add(
                             ChangeRequest(
                                 value = PatientIdentifier(
                                     identifierType = IdentificationConstants.VOTER_ID_TYPE,
                                     identifierNumber = voterId,
                                     code = null
                                 ), operation = ChangeTypeEnum.ADD.value,
                                 key = IdentificationConstants.VOTER_ID_TYPE
                             )

                         )

                     }

                     if (patientId != patientIdTemp && patientId.isEmpty() && patientIdTemp.isNotEmpty()) {
                         list.add(
                             ChangeRequest(
                                 value = PatientIdentifier(
                                     identifierType = IdentificationConstants.PATIENT_ID_TYPE,
                                     identifierNumber = patientIdTemp,
                                     code = null
                                 ), operation = ChangeTypeEnum.REMOVE.value,
                                 key = IdentificationConstants.PATIENT_ID_TYPE
                             )

                         )

                     } else if (patientId != patientIdTemp && patientIdTemp.isNotEmpty() && patientId.isNotEmpty()) {
                         list.add(
                             ChangeRequest(
                                 value = PatientIdentifier(
                                     identifierType = IdentificationConstants.PATIENT_ID_TYPE,
                                     identifierNumber = patientId,
                                     code = null
                                 ), operation = ChangeTypeEnum.REPLACE.value,
                                 key = IdentificationConstants.PATIENT_ID_TYPE
                             )
                         )

                     } else if (patientId != patientIdTemp && patientIdTemp.isEmpty() && patientId.isNotEmpty()) {
                         list.add(
                             ChangeRequest(
                                 value = PatientIdentifier(
                                     identifierType = IdentificationConstants.PATIENT_ID_TYPE,
                                     identifierNumber = patientId,
                                     code = null
                                 ), operation = ChangeTypeEnum.ADD.value,
                                 key = IdentificationConstants.PATIENT_ID_TYPE
                             )

                         )

                     }

                     genericRepository.insertOrUpdatePatchEntity(
                         patientFhirId = patientResponse.fhirId,
                         map = mapOf(
                             Pair(
                                 "identifier",
                                 list
                             )
                         ),
                         typeEnum = GenericTypeEnum.PATIENT
                     )

                     Timber.tag("identifier").d(list.toJson())
                 } else {
                     genericRepository.insertOrUpdatePostEntity(
                         patientId = patientResponse.id,
                         entity = patientResponse,
                         typeEnum = GenericTypeEnum.PATIENT
                     )
                 }
             }
         }
    }
}