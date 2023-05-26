package com.latticeonfhir.android.data.server.model.prescription.prescriptionresponse

import com.google.errorprone.annotations.Keep

@Keep
data class Medication(
    val doseForm: String,
    val duration: Int,
    val frequency: Int,
    val medFhirId: String,
    val note: String,
    val qtyPerDose: String,
    val qtyPrescribed: Int,
    val timing: String
)