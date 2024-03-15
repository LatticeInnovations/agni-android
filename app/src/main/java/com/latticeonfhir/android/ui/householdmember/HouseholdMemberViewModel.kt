package com.latticeonfhir.android.ui.householdmember

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import org.hl7.fhir.r4.model.Patient

class HouseholdMemberViewModel : BaseViewModel() {
    var isLaunched by mutableStateOf(false)

    val tabs = listOf("Members", "Suggestions")

    var isUpdateSelected by mutableStateOf(false)

    var patient by mutableStateOf(Patient())
}