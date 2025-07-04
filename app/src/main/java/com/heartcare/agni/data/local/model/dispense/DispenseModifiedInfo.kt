package com.heartcare.agni.data.local.model.dispense

import androidx.annotation.Keep
import com.heartcare.agni.data.local.roomdb.views.PrescriptionDirectionAndMedicineView

@Keep
data class DispenseModifiedInfo (
    var qtyToBeDispensed: Int,
    var note: String,
    var qtyLeft: Int,
    var isModified: Boolean,
    val medication: PrescriptionDirectionAndMedicineView
)