package com.latticeonfhir.features.prescription.data.model

import androidx.annotation.Keep
import com.latticeonfhir.android.data.server.model.prescription.prescriptionresponse.Medication

@Keep
data class MedicationResponseWithMedication(
    val activeIngredient: String,
    val medName: String,
    val medUnit: String,
    val medication: Medication
)