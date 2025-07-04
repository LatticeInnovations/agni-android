package com.heartcare.agni.data.local.model.prescription.medication

import androidx.annotation.Keep
import com.heartcare.agni.data.server.model.prescription.prescriptionresponse.Medication

@Keep
data class MedicationResponseWithMedication(
    val activeIngredient: String,
    val medName: String,
    val medUnit: String,
    val medication: Medication
)