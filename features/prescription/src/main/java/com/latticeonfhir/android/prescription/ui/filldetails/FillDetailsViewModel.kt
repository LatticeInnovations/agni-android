package com.latticeonfhir.android.prescription.ui.filldetails

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import com.latticeonfhir.android.data.local.repository.medication.MedicationRepository
import com.latticeonfhir.android.data.server.model.prescription.medication.MedicationResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
    var timing by mutableStateOf("")
    var duration by mutableStateOf("")
    var notes by mutableStateOf("")
    var medUnit by mutableStateOf("")
    var medDoseForm by mutableStateOf("")
    var medFhirId by mutableStateOf("")
    var isDurationInvalid by mutableStateOf(false)

    internal fun quantityPrescribed(): String {
        return if (duration.isBlank() || isDurationInvalid) ""
        else (quantityPerDose.toInt() * frequency.toInt() * duration.toInt()).toString()
    }

    internal fun reset() {
        medSelected = ""
        quantityPerDose = "1"
        frequency = "1"
        duration = ""
        notes = ""
        timing = ""
        medFhirId = ""
        medDoseForm = ""
        medUnit = ""
        isDurationInvalid = false
    }

    internal fun getMedicationByActiveIngredient(
        activeIngredientName: String,
        formulationsList: (List<MedicationResponse>) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            formulationsList(
                medicationRepository.getMedicationByActiveIngredient(activeIngredientName)
            )
        }
    }
}