package com.latticeonfhir.core.data.local.model.dispense

import androidx.annotation.Keep
import com.latticeonfhir.android.data.local.roomdb.views.PrescriptionDirectionAndMedicineView
import com.latticeonfhir.core.database.views.PrescriptionDirectionAndMedicineView

@Keep
data class DispenseModifiedInfo (
    var qtyToBeDispensed: Int,
    var note: String,
    var qtyLeft: Int,
    var isModified: Boolean,
    val medication: PrescriptionDirectionAndMedicineView
)