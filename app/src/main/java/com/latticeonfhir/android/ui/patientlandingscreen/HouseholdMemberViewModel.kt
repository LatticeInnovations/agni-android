package com.latticeonfhir.android.ui.main.patientlandingscreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.server.model.patient.PatientResponse

class HouseholdMemberViewModel: BaseViewModel() {

    var tabIndex by mutableStateOf(0)

    val tabs = listOf("Members", "Suggestions")

    var isUpdateSelected by mutableStateOf(false)

    var patient by mutableStateOf<PatientResponse?>(null)
}