package com.latticeonfhir.android.ui.patienteditscreen.identification

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.viewModelScope
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.logicalId
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.utils.constants.patient.IdentificationConstants.LATTICE
import com.latticeonfhir.android.utils.constants.patient.IdentificationConstants.PASSPORT_TYPE
import com.latticeonfhir.android.utils.constants.patient.IdentificationConstants.PATIENT_ID_TYPE
import com.latticeonfhir.android.utils.constants.patient.IdentificationConstants.VOTER_ID_TYPE
import com.latticeonfhir.android.utils.fhirengine.FhirQueries.getPersonResource
import com.latticeonfhir.android.utils.fhirengine.FhirQueries.isIdDuplicate
import com.latticeonfhir.android.utils.regex.PassportRegex
import com.latticeonfhir.android.utils.regex.VoterRegex
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Identifier
import org.hl7.fhir.r4.model.Patient
import javax.inject.Inject

@HiltViewModel
class EditIdentificationViewModel @Inject constructor(
    private val fhirEngine: FhirEngine
) : BaseViewModel(), DefaultLifecycleObserver {
    var isLaunched by mutableStateOf(false)
    var isEditing by mutableStateOf(false)
    var isUpdating by mutableStateOf(false)
    var patient by mutableStateOf(Patient())

    val maxPassportIdLength = 8
    val maxVoterIdLength = 10
    val maxPatientIdLength = 10

    var isPassportSelected by mutableStateOf(true)
    var isVoterSelected by mutableStateOf(false)
    var isPatientSelected by mutableStateOf(false)
    var passportId by mutableStateOf("")
    var voterId by mutableStateOf("")
    var patientId by mutableStateOf("")

    val passportPattern = PassportRegex.passportPattern
    val voterPattern = VoterRegex.voterPattern
    var isPassportValid by mutableStateOf(false)
    var isVoterValid by mutableStateOf(false)
    var isPatientValid by mutableStateOf(false)

    // temp
    private var isPassportSelectedTemp by mutableStateOf(false)
    private var isVoterSelectedTemp by mutableStateOf(false)
    private var isPatientSelectedTemp by mutableStateOf(false)
    private var passportIdTemp by mutableStateOf("")
    private var voterIdTemp by mutableStateOf("")
    private var patientIdTemp by mutableStateOf("")


    fun identityInfoValidation(): Boolean {
        if (!isPassportSelected && !isVoterSelected && !isPatientSelected)
            return false
        if (isPassportSelected && !passportPattern.matches(passportId))
            return false
        if (isVoterSelected && !voterPattern.matches(voterId))
            return false
        return !(isPatientSelected && patientId.length < 10)
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
        isPassportValid = false
        isVoterValid = false
        isPatientValid = false
        return true
    }

    fun updateIdentifierInfo(updated: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val latticeId = patient.identifier.first {
                it.system == LATTICE
            }
            val patient = patient.apply {
                    identifier.clear()
                    identifier.add(
                        latticeId
                    )
                    if (isPassportSelected) {
                        identifier.add(
                            Identifier().apply {
                                system = PASSPORT_TYPE
                                value = passportId
                                use = if (isIdDuplicate(
                                        fhirEngine,
                                        PASSPORT_TYPE,
                                        passportId,
                                        patient.logicalId
                                    )
                                ) Identifier.IdentifierUse.TEMP else Identifier.IdentifierUse.OFFICIAL
                            }
                        )
                    }
                    if (isVoterSelected) {
                        identifier.add(
                            Identifier().apply {
                                system = VOTER_ID_TYPE
                                value = voterId
                                use = if (isIdDuplicate(
                                        fhirEngine,
                                        VOTER_ID_TYPE,
                                        voterId,
                                        patient.logicalId
                                    )
                                ) Identifier.IdentifierUse.TEMP else Identifier.IdentifierUse.OFFICIAL
                            }
                        )
                    }
                    if (isPatientSelected) {
                        identifier.add(
                            Identifier().apply {
                                system = PATIENT_ID_TYPE
                                value = patientId
                                use = if (isIdDuplicate(
                                        fhirEngine,
                                        PATIENT_ID_TYPE,
                                        patientId,
                                        patient.logicalId
                                    )
                                ) Identifier.IdentifierUse.TEMP else Identifier.IdentifierUse.OFFICIAL
                            }
                        )
                    }
                }
            val person = getPersonResource(fhirEngine, patient.logicalId).apply {
                identifier.clear()
                identifier = patient.identifier
            }
            fhirEngine.update(patient, person)
            updated()
        }
    }

    internal fun setData() {
        patient.run {
            identifier.forEach { id ->
                when (id.system) {
                    PASSPORT_TYPE -> {
                        passportId = id.value
                    }

                    VOTER_ID_TYPE -> {
                        voterId = id.value
                    }

                    PATIENT_ID_TYPE -> {
                        patientId = id.value
                    }
                }
            }
        }
        if (isEditing) {
            isPassportSelected = passportId.isNotBlank()
            isVoterSelected = voterId.isNotBlank()
            isPatientSelected = patientId.isNotBlank()
        }
        isPassportSelectedTemp = isPassportSelected
        isVoterSelectedTemp = isVoterSelected
        isPatientSelectedTemp = isPatientSelected
        passportIdTemp = passportId
        voterIdTemp = voterId
        patientIdTemp = patientId
    }
}