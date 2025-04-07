package com.latticeonfhir.android.data.server.model.create

import androidx.annotation.Keep

@Keep
data class DocumentIdResponse(
    val documentfhirId: String,
    val documentUuid: String
)
