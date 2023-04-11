package com.latticeonfhir.android.ui.patientlandingscreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.server.model.patient.PatientResponse

class PatientLandingScreenViewModel: BaseViewModel() {
    var patient by mutableStateOf<PatientResponse?>(null)
}