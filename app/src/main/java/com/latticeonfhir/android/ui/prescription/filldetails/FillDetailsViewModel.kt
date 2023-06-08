package com.latticeonfhir.android.ui.prescription.filldetails

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.repository.medication.MedicationRepository
import com.latticeonfhir.android.data.server.model.prescription.medication.MedicationResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FillDetailsViewModel @Inject constructor(
    private val medicationRepository: MedicationRepository
) : BaseViewModel() {
    var isLaunched by mutableStateOf(false)
    var formulationsList by mutableStateOf(listOf<MedicationResponse>())
    var medSelected by mutableStateOf("")

    var quantityPerDose by mutableStateOf("1")
    var frequency by mutableStateOf("1")
    val qtyRange = 1..10
    var timing by mutableStateOf("Before food")
    var duration by mutableStateOf("")
    var notes by mutableStateOf("")
    var medUnit by mutableStateOf("")
    var medDoseForm by mutableStateOf("")
    var medFhirId by mutableStateOf("")

    internal fun quantityPrescribed(): String{
        return if (duration.isBlank()) ""
         else (quantityPerDose.toInt() * frequency.toInt() * duration.toInt()).toString()
    }

    internal fun reset(){
        medSelected = ""
        quantityPerDose = "1"
        frequency = "1"
        duration = ""
        notes = ""
        timing = "Before food"
    }

    internal fun getMedicationByActiveIngredient(activeIngredientName: String){
        viewModelScope.launch {
            formulationsList = medicationRepository.getMedicationByActiveIngredient(activeIngredientName)
        }
    }
}