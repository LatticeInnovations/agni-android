package com.latticeonfhir.android.ui.householdmember

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.server.model.patient.PatientResponse

class HouseholdMemberViewModel : BaseViewModel() {
    var isLaunched by mutableStateOf(false)

    val tabs = listOf("Members", "Suggestions")

    var selectedIndex by mutableIntStateOf(0)

    var isUpdateSelected by mutableStateOf(false)

    var patient by mutableStateOf<PatientResponse?>(null)
}