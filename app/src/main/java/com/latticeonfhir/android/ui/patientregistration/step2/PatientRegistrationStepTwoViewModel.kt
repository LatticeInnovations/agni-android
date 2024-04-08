package com.latticeonfhir.android.ui.patientregistration.step2

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import com.google.android.fhir.FhirEngine
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.repository.preference.PreferenceRepository
import com.latticeonfhir.android.utils.constants.patient.IdentificationConstants.LATTICE
import com.latticeonfhir.android.utils.constants.patient.IdentificationConstants.PASSPORT_TYPE
import com.latticeonfhir.android.utils.constants.patient.IdentificationConstants.PATIENT_ID_TYPE
import com.latticeonfhir.android.utils.constants.patient.IdentificationConstants.VOTER_ID_TYPE
import dagger.hilt.android.lifecycle.HiltViewModel
import org.hl7.fhir.r4.model.Patient
import javax.inject.Inject

@HiltViewModel
class PatientRegistrationStepTwoViewModel @Inject constructor(
    val fhirEngine: FhirEngine,
    val preferenceRepository: PreferenceRepository
) : BaseViewModel(), DefaultLifecycleObserver {
    var isLaunched by mutableStateOf(false)

    val maxPassportIdLength = 8
    val maxVoterIdLength = 10
    val maxPatientIdLength = 10

    var isPassportSelected by mutableStateOf(true)
    var isVoterSelected by mutableStateOf(false)
    var isPatientSelected by mutableStateOf(false)
    var passportId by mutableStateOf("")
    var voterId by mutableStateOf("")
    var patientId by mutableStateOf("")
    var latticeId by mutableStateOf("")

    val passportPattern = Regex("^[A-PR-WYa-pr-wy][1-9]\\d\\s?\\d{4}[1-9]$")
    val voterPattern = Regex("^[A-Za-z]{3}[0-9]{7}$")
    var isPassportValid by mutableStateOf(false)
    var isVoterValid by mutableStateOf(false)
    var isPatientValid by mutableStateOf(false)

    fun identityInfoValidation(): Boolean {
        if (!isPassportSelected && !isVoterSelected && !isPatientSelected)
            return false
        if (isPassportSelected && !passportPattern.matches(passportId))
            return false
        if (isVoterSelected && !voterPattern.matches(voterId))
            return false
        return !(isPatientSelected && patientId.length < 10)
    }

    fun generateLatticeId() {
        if (latticeId.isBlank()) latticeId = "${preferenceRepository.getUserSessionId()}-${
            preferenceRepository.getPatientId().ifEmpty { "001" }
        }"
    }
    
    internal fun setData(patient: Patient) {
        isPassportSelected = false
        patient.run {
            identifier.forEach { id ->
                when(id.system) {
                    LATTICE -> {
                        latticeId = id.value
                    }
                    PASSPORT_TYPE -> {
                        passportId = id.value
                        isPassportSelected = passportId.isNotBlank()
                    }
                    VOTER_ID_TYPE -> {
                        voterId = id.value
                        isVoterSelected = voterId.isNotBlank()
                    }
                    PATIENT_ID_TYPE -> {
                        patientId = id.value
                        isPatientSelected = patientId.isNotBlank()
                    }
                }
            }
        }
    }
}