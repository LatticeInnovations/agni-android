package com.latticeonfhir.android.ui.patientprofile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.logicalId
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.ResourceType
import javax.inject.Inject

@HiltViewModel
class PatientProfileViewModel @Inject constructor(
    private val fhirEngine: FhirEngine
) : BaseViewModel() {

    internal var isLaunched by mutableStateOf(false)
    internal var isProfileUpdated by mutableStateOf(false)
    internal var patient by mutableStateOf(Patient())

    internal fun getPatientData() {
        viewModelScope.launch(Dispatchers.IO) {
            patient = fhirEngine.get(ResourceType.Patient, patient.logicalId) as Patient
        }
    }
}