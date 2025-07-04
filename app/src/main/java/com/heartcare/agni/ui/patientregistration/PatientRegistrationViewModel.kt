package com.heartcare.agni.ui.patientregistration

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.server.model.patient.PatientResponse

class PatientRegistrationViewModel : BaseViewModel(), DefaultLifecycleObserver {
    var isLaunched by mutableStateOf(false)

    var currentStep by mutableStateOf(1)
    var totalSteps by mutableStateOf(3)
    var isEditing by mutableStateOf(false)
    var fromHouseholdMember by mutableStateOf(false)
    var showRelationDialogue by mutableStateOf(false)
    var relation by mutableStateOf("")
    var patientFrom by mutableStateOf<PatientResponse?>(null)
    var openDialog by mutableStateOf(false)
}