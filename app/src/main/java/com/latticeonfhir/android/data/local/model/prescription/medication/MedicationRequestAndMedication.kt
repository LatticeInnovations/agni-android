package com.latticeonfhir.android.data.local.model.prescription.medication

import androidx.annotation.Keep
import org.hl7.fhir.r4.model.Medication
import org.hl7.fhir.r4.model.MedicationRequest

@Keep
data class MedicationRequestAndMedication (
    val medication: Medication,
    val medicationRequest: MedicationRequest
)