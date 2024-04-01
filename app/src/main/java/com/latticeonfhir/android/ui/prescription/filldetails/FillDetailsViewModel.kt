package com.latticeonfhir.android.ui.prescription.filldetails

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.latticeonfhir.android.base.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.hl7.fhir.r4.model.Medication
import javax.inject.Inject

@HiltViewModel
class FillDetailsViewModel @Inject constructor() : BaseViewModel() {
    var isLaunched by mutableStateOf(false)
    var formulationsList by mutableStateOf(listOf<Medication>())
    var medSelected by mutableStateOf("")
    var medicationSelected by mutableStateOf<Medication?>(null)

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
    
    internal fun setData(formulation: Medication) {
        medicationSelected = formulation
        medSelected = formulation.code.codingFirstRep.display
        medUnit = formulation.ingredientFirstRep.strength.denominator.code
        medDoseForm = formulation.form.text
        medFhirId = formulation.code.codingFirstRep.code
    }

    internal fun getMedicationByActiveIngredient(
        activeIngredientName: String,
        medicationList: List<Medication>
    ) {
        formulationsList = if (activeIngredientName.contains("+")){
            medicationList.filter {
                it.ingredient.size > 1
            }.filter {
                it.ingredient[0].itemCodeableConcept.codingFirstRep.display == activeIngredientName.substringBefore("+") &&
                        it.ingredient[1].itemCodeableConcept.codingFirstRep.display == activeIngredientName.substringAfter("+")
            }
        } else {
            medicationList.filter {
                it.ingredient.size == 1
            }.filter {
                it.ingredientFirstRep.itemCodeableConcept.codingFirstRep.display == activeIngredientName
            }
        }
    }
}