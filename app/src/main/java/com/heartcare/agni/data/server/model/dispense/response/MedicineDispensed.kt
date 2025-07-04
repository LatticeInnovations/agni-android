package com.heartcare.agni.data.server.model.dispense.response

import androidx.annotation.Keep
import java.util.Date

@Keep
data class MedicineDispensed(
    val category: String,
    val date: Date,
    val isModified: Boolean,
    val modificationType: String?,
    val medDispenseFhirId: String,
    val medDispenseUuid: String,
    val medFhirId: String,
    val medNote: String?,
    val medReqFhirId: String?,
    val patientId: String,
    val prescriptionData: PrescriptionData?,
    val qtyDispensed: Int,
    val dispensedMedication: DispensedMedication
)