package com.latticeonfhir.android.data.server.model.create

import androidx.annotation.Keep

@Keep
data class MedDocumentIdResponse(
    val medicalDocumentfhirId: String,
    val medicalDocumentUuid: String
)