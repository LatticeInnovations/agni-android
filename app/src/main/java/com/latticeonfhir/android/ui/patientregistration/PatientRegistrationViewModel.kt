package com.latticeonfhir.android.ui.patientregistration

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import org.hl7.fhir.r4.model.Patient

class PatientRegistrationViewModel : BaseViewModel(), DefaultLifecycleObserver {
    var isLaunched by mutableStateOf(false)
    var showLoader by mutableStateOf(false)

    var patient by mutableStateOf(Patient())

    var currentStep by mutableIntStateOf(1)
    var totalSteps by mutableIntStateOf(3)
    var isEditing by mutableStateOf(false)
    var fromHouseholdMember by mutableStateOf(false)
    var showRelationDialogue by mutableStateOf(false)
    var relation by mutableStateOf("")
    var patientFrom by mutableStateOf(Patient())
    var openDialog by mutableStateOf(false)
}