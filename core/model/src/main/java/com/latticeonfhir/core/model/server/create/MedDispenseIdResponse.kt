package com.latticeonfhir.core.model.server.create

import androidx.annotation.Keep

@Keep
data class MedDispenseIdResponse(
    val medDispenseUuid: String,
    val medDispenseFhirId: String
)