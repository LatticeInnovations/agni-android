package com.latticeonfhir.android.data.server.model.dispense.request

import androidx.annotation.Keep

@Keep
data class MedicineDispensed(
    val medDispenseUuid: String,
    val category: String,
    val isModified: Boolean,
    val medFhirId: String,
    val medNote: String?,
    val medReqFhirId: String?,
    val modificationType: String?,
    val qtyDispensed: Int
)