package com.latticeonfhir.core.model.local.dispense

import androidx.annotation.Keep
import com.latticeonfhir.core.model.entity.prescription.PrescriptionDirectionAndMedicineView

@Keep
data class DispenseModifiedInfo (
    var qtyToBeDispensed: Int,
    var note: String,
    var qtyLeft: Int,
    var isModified: Boolean,
    val medication: PrescriptionDirectionAndMedicineView
)