package com.heartcare.agni.data.server.model.dispense.response

import androidx.annotation.Keep
import java.util.Date

@Keep
data class DispenseData(
    val dispenseFhirId: String,
    val dispenseId: String,
    val appointmentId: String?,
    val generatedOn: Date,
    val note: String?,
    val patientId: String,
    val type: String,
    val medicineDispensedList: List<MedicineDispensed>
)