package com.latticeonfhir.android.ui.prescription.filldetails

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.latticeonfhir.android.base.viewmodel.BaseViewModel

class FillDetailsViewModel : BaseViewModel() {
    var isLaunched by mutableStateOf(false)
    var drugName by mutableStateOf("")
    var formulationsList = mutableStateListOf(
        "Epinephrine tartrate 100 micrograms/mL injection",
        "Epinephrine hydrochloride 100 micrograms/mL injection",
        "Epinephrine tartrate 1 mg/mL injection",
        "Epinephrine hydrochloride 1 mg/mL injection",
        "Epinephrine hydrochloride 2.5 mg/ml solution eye solution"
    )
    var formulationSelected by mutableStateOf("")

    var quantityPerDose by mutableStateOf("1")
    var frequency by mutableStateOf("1")
    var timing by mutableStateOf("Before food")
    var duration by mutableStateOf("7")
    var quantityPrescribed by mutableStateOf("7")
    var notes by mutableStateOf("")
}