package com.latticeonfhir.core.model.server.create

import androidx.annotation.Keep

@Keep
data class DocumentIdResponse(
    val documentfhirId: String,
    val documentUuid: String
)
