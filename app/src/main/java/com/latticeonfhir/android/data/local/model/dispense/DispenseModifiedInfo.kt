package com.latticeonfhir.android.data.local.model.dispense

import androidx.annotation.Keep
import com.latticeonfhir.android.data.local.roomdb.views.PrescriptionDirectionAndMedicineView

@Keep
data class DispenseModifiedInfo (
    var qtyToBeDispensed: Int,
    var note: String,
    var qtyLeft: Int,
    var isModified: Boolean,
    val medication: PrescriptionDirectionAndMedicineView
)