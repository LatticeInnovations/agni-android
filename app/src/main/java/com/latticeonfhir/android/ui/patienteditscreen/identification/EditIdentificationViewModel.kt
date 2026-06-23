package com.latticeonfhir.android.ui.patienteditscreen.identification

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.enums.ChangeTypeEnum
import com.latticeonfhir.android.data.local.model.patch.ChangeRequest
import com.latticeonfhir.android.data.local.repository.generic.GenericRepository
import com.latticeonfhir.android.data.local.repository.identifier.IdentifierRepository
import com.latticeonfhir.android.data.local.repository.patient.PatientRepository
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

    val maxAbhaIdLength = 14
    val maxRationCardLength = 20

    var isAbhaSelected by mutableStateOf(false)
    var isRationCardSelected by mutableStateOf(false)
    var abhaId by mutableStateOf("")
    var rationCard by mutableStateOf("")

    val abhaRegex = Regex("^\\d{14}$")
    val rationCardRegex = Regex("^[A-Z0-9]{20}$")
    var isAbhaIdValid by mutableStateOf(false)
    var isRationCardValid by mutableStateOf(false)

    val identifierList = mutableListOf<PatientIdentifier>()
    var patient by mutableStateOf<PatientResponse?>(null)

    // temp
    var isAbhaSelectedTemp by mutableStateOf(false)
    var isRationCardSelectedTemp by mutableStateOf(false)
    var abhaIdTemp by mutableStateOf("")
    var rationCardTemp by mutableStateOf("")

    fun identityInfoValidation(): Boolean {
        if (isAbhaSelected && !abhaRegex.matches(abhaId))
            return false
        if (isRationCardSelected && !rationCardRegex.matches(rationCard))
            return false
        return true
    }

    fun checkIsEdit(): Boolean {
        return isAbhaSelected != isAbhaSelectedTemp ||
                isRationCardSelected != isRationCardSelectedTemp ||
                abhaId != abhaIdTemp ||
                rationCard != rationCardTemp
    }

    fun revertChanges(): Boolean {
        isAbhaSelected = isAbhaSelectedTemp
        isRationCardSelected = isRationCardSelectedTemp
        abhaId = abhaIdTemp
        rationCard = rationCardTemp
        isAbhaIdValid = false
        isRationCardValid = false
        return true
    }

    fun updateBasicInfo(patientResponse: PatientResponse) {
        viewModelScope.launch(Dispatchers.IO) {
            val toBeDeletedList = mutableListOf<PatientIdentifier>()
            if (abhaIdTemp != abhaId || !isAbhaSelected) {
                toBeDeletedList.add(
                    PatientIdentifier(
                        identifierType = IdentificationConstants.ABHA_ID_TYPE,
                        identifierNumber = abhaIdTemp,
                        code = null
                    )
                )
            }

            if (rationCardTemp != rationCard || !isRationCardSelected) {
                toBeDeletedList.add(
                    PatientIdentifier(
                        identifierType = IdentificationConstants.RATION_CARD_TYPE,
                        identifierNumber = rationCardTemp,
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

                    if (abhaId != abhaIdTemp && abhaId.isEmpty() && abhaIdTemp.isNotEmpty()) {


                        list.add(
                            ChangeRequest(
                                value = PatientIdentifier(
                                    identifierType = IdentificationConstants.ABHA_ID_TYPE,
                                    identifierNumber = abhaIdTemp,
                                    code = null
                                ), operation = ChangeTypeEnum.REMOVE.value,
                                key = IdentificationConstants.ABHA_ID_TYPE
                            )

                        )

                    } else if (abhaId != abhaIdTemp && abhaIdTemp.isNotEmpty() && abhaId.isNotEmpty()) {
                        list.add(
                            ChangeRequest(
                                value = PatientIdentifier(
                                    identifierType = IdentificationConstants.ABHA_ID_TYPE,
                                    identifierNumber = abhaId,
                                    code = null
                                ), operation = ChangeTypeEnum.REPLACE.value,
                                key = IdentificationConstants.ABHA_ID_TYPE
                            )

                        )

                    } else if (abhaId != abhaIdTemp && abhaIdTemp.isEmpty() && abhaId.isNotEmpty()) {
                        list.add(
                            ChangeRequest(
                                value = PatientIdentifier(
                                    identifierType = IdentificationConstants.ABHA_ID_TYPE,
                                    identifierNumber = abhaId,
                                    code = null
                                ), operation = ChangeTypeEnum.ADD.value,
                                key = IdentificationConstants.ABHA_ID_TYPE
                            )

                        )

                    }

                    if (rationCard != rationCardTemp && rationCard.isEmpty() && rationCardTemp.isNotEmpty()) {
                        list.add(
                            ChangeRequest(
                                value = PatientIdentifier(
                                    identifierType = IdentificationConstants.RATION_CARD_TYPE,
                                    identifierNumber = rationCardTemp,
                                    code = null
                                ), operation = ChangeTypeEnum.REMOVE.value,
                                key = IdentificationConstants.RATION_CARD_TYPE
                            )

                        )

                    } else if (rationCard != rationCardTemp && rationCardTemp.isNotEmpty() && rationCard.isNotEmpty()) {
                        list.add(
                            ChangeRequest(
                                value = PatientIdentifier(
                                    identifierType = IdentificationConstants.RATION_CARD_TYPE,
                                    identifierNumber = rationCard,
                                    code = null
                                ), operation = ChangeTypeEnum.REPLACE.value,
                                key = IdentificationConstants.RATION_CARD_TYPE
                            )
                        )

                    } else if (rationCard != rationCardTemp && rationCardTemp.isEmpty() && rationCard.isNotEmpty()) {
                        list.add(
                            ChangeRequest(
                                value = PatientIdentifier(
                                    identifierType = IdentificationConstants.RATION_CARD_TYPE,
                                    identifierNumber = rationCard,
                                    code = null
                                ), operation = ChangeTypeEnum.ADD.value,
                                key = IdentificationConstants.RATION_CARD_TYPE
                            )

                        )

                    }

                    genericRepository.insertOrUpdatePatientPatchEntity(
                        patientFhirId = patientResponse.fhirId,
                        map = mapOf(
                            Pair(
                                "identifier",
                                list
                            )
                        )
                    )

                    Timber.tag("identifier").d(list.toJson())
                } else {
                    genericRepository.insertPatient(
                        patientResponse
                    )
                }
            }
        }
    }
}