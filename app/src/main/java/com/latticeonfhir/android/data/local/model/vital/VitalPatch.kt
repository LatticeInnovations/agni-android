package com.latticeonfhir.core.data.local.model.vital

import androidx.annotation.Keep

@Keep
data class VitalPatch<T>(
    val vitalFhirId: String,
    val key: String,
    val component: T
)
