package com.latticeonfhir.core.data.local.model.prescription.medication

import androidx.annotation.Keep
import com.latticeonfhir.android.data.server.model.prescription.prescriptionresponse.Medication

@Keep
data class MedicationResponseWithMedication(
    val activeIngredient: String,
    val medName: String,
    val medUnit: String,
    val medication: Medication
)