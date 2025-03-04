package com.latticeonfhir.android.ui.vaccination

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.latticeonfhir.android.data.server.model.patient.PatientResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VaccinationViewModel @Inject constructor(

): ViewModel() {
    var isLaunched by mutableStateOf(false)
    var patient by mutableStateOf<PatientResponse?>(null)

    val tabs = listOf("All", "Missed", "Taken")

    var missedVaccinesList by mutableStateOf(listOf(""))
    var takenVaccinesList by mutableStateOf(listOf(""))

    companion object {
        const val MISSED = "missed"
        const val TAKEN = "taken"
    }
}