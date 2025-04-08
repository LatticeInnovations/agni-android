package com.latticeonfhir.android.data.server.model.dispense.response

import androidx.annotation.Keep

@Keep
data class PrescriptionData(
    val doseForm: String,
    val doseFormCode: String,
    val duration: Int,
    val frequency: Int,
    val medFhirId: String,
    val medReqFhirId: String,
    val note: String,
    val prescribedMedication: DispensedMedication,
    val qtyPerDose: Int,
    val qtyPrescribed: Int,
    val timing: String
)