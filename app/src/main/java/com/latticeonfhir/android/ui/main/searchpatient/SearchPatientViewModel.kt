package com.latticeonfhir.android.ui.main.searchpatient

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class SearchPatientViewModel: ViewModel() {
    var step by mutableStateOf(1)
    var patientName by mutableStateOf("")
    var patientId by mutableStateOf("")
    var gender by mutableStateOf("")
    var minAge by mutableStateOf("20")
    var maxAge by mutableStateOf("40")
    val visitIntervals =
        listOf("Last week", "Last month", "Last 3 months", "Last year")
    var visitSelected by mutableStateOf(visitIntervals[0])

    var range by mutableStateOf(minAge.toFloat()..maxAge.toFloat())
}