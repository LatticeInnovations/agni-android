package com.latticeonfhir.android.data.server.model.prescription.photo

import androidx.annotation.Keep

@Keep
data class PrescriptionPhotoPatch(
    val documentFhirId: String,
    val note: String,
    val filename: String
)
