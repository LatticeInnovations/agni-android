package com.latticeonfhir.android.ui.patientregistration

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.DefaultLifecycleObserver
import com.latticeonfhir.android.base.viewmodel.BaseViewModel

class PatientRegistrationViewModel: BaseViewModel(), DefaultLifecycleObserver {
    var step by mutableStateOf(1)
    var isEditing by mutableStateOf(false)
}