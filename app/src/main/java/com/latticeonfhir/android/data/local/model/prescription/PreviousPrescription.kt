package com.latticeonfhir.android.data.local.model.prescription

import androidx.annotation.Keep
import com.latticeonfhir.android.data.local.model.prescription.medication.MedicationRequestAndMedication
import org.hl7.fhir.r4.model.Encounter

@Keep
data class PreviousPrescription (
    val encounter: Encounter,
    val medicationRequestList: List<MedicationRequestAndMedication>
)