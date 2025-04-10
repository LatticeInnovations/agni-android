package com.latticeonfhir.core.data.local.model.prescription

import androidx.annotation.Keep
import java.util.Date

@Keep
data class PrescriptionResponseLocal(
    val patientId: String,
    val patientFhirId: String?,
    val appointmentId: String,
    val generatedOn: Date,
    val prescriptionId: String,
    val prescription: List<MedicationLocal>
)

@Keep
data class MedicationLocal(
    val medReqUuid: String,
    val medReqFhirId: String?,
    val doseForm: String,
    val duration: Int,
    val frequency: Int,
    val medFhirId: String,
    val note: String?,
    val qtyPerDose: Int,
    val qtyPrescribed: Int,
    val timing: String?,
    val medName: String,
    val medUnit: String
)