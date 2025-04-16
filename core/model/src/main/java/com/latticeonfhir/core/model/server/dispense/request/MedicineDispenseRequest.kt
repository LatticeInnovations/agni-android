package com.latticeonfhir.core.model.server.dispense.request

import androidx.annotation.Keep
import java.util.Date

@Keep
data class MedicineDispenseRequest(
    val dispenseId: String,
    val appointmentId: String,
    val generatedOn: Date,
    val note: String?,
    val patientId: String,
    val prescriptionFhirId: String?,
    val status: String?,
    val medicineDispensedList: List<MedicineDispensed>
)