package com.latticeonfhir.android.data.server.model.dispense.response

import androidx.annotation.Keep
import com.latticeonfhir.android.data.server.model.prescription.medication.Strength

@Keep
data class DispensedMedication(
    val activeIngredient: String,
    val activeIngredientCode: String,
    val doseForm: String,
    val doseFormCode: String,
    val medCode: String,
    val medFhirId: String,
    val medName: String,
    val medNumeratorVal: String,
    val medUnit: String,
    val strength: List<Strength>
)