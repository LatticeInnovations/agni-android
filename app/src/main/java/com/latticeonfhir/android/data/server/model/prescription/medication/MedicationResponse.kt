package com.latticeonfhir.android.data.server.model.prescription.medication

import androidx.annotation.Keep

@Keep
data class MedicationResponse(
    val activeIngredient: String,
    val activeIngredientCode: String,
    val doseForm: String,
    val doseFormCode: String,
    val medCode: String,
    val medFhirId: String,
    val medName: String,
    val medNumeratorVal: Double,
    val medUnit: String
)