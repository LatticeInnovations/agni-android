package com.heartcare.agni.ui.historyandtests

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.heartcare.agni.data.server.model.patient.PatientResponse

class HistoryTakingAndTestsViewModel: ViewModel() {
    var isLaunched by mutableStateOf(false)

    var patient by mutableStateOf<PatientResponse?>(null)
}