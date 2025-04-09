package com.latticeonfhir.core.data.server.model.prescription.medication

import androidx.annotation.Keep

@Keep
data class Strength(
    val medName: String,
    val unitMeasureValue: String,
    val medMeasureCode: String
)
