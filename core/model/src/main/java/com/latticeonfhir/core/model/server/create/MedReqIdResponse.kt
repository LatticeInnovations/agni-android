package com.latticeonfhir.core.data.server.model.create

import androidx.annotation.Keep

@Keep
data class MedReqIdResponse(
    val medReqUuid: String,
    val medReqFhirId: String
)
