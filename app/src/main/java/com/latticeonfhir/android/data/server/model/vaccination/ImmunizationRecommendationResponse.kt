package com.latticeonfhir.android.data.server.model.vaccination

import androidx.annotation.Keep
import java.util.Date

@Keep
data class ImmunizationRecommendationResponse(
    val patientId: String,
    val vaccine: String,
    val vaccineText: String,
    val vaccineCode: String,
    val seriesDoses: Int,
    val doseNumber: Int,
    val vaccineStartDate: Date,
    val vaccineEndDate: Date,
    val vaccineBufferDate: Date
)
