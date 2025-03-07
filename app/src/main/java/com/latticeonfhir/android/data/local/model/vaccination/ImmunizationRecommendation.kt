package com.latticeonfhir.android.data.local.model.vaccination

import androidx.annotation.Keep
import java.util.Date

@Keep
data class ImmunizationRecommendation(
    val id: String,
    val name: String,
    val shortName: String,
    val seriesDoses: Int,
    val doseNumber: Int,
    val vaccineStartDate: Date,
    val vaccineEndDate: Date,
    val takenOn: Date?
)
