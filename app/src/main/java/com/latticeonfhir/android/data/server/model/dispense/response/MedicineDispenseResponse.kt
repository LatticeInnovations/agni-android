package com.latticeonfhir.android.data.server.model.dispense.response

import androidx.annotation.Keep

@Keep
data class MedicineDispenseResponse(
    val patientId: String,
    val prescriptionFhirId: String,
    val status: String,
    val dispenseData: List<DispenseData>
)