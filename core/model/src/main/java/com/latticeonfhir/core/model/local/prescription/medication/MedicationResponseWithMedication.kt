package com.latticeonfhir.core.model.local.prescription.medication

import androidx.annotation.Keep
import com.latticeonfhir.core.model.server.prescription.prescriptionresponse.Medication

@Keep
data class MedicationResponseWithMedication(
    val activeIngredient: String,
    val medName: String,
    val medUnit: String,
    val medication: Medication
)