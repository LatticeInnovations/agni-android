package com.latticeonfhir.core.data.server.model.create

import androidx.annotation.Keep

@Keep
data class MedDispenseIdResponse(
    val medDispenseUuid: String,
    val medDispenseFhirId: String
)