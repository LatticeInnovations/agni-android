package com.latticeonfhir.core.model.server.create

import androidx.annotation.Keep

@Keep
data class MedReqIdResponse(
    val medReqUuid: String,
    val medReqFhirId: String
)
