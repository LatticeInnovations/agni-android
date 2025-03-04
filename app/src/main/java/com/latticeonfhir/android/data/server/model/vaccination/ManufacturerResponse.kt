package com.latticeonfhir.android.data.server.model.vaccination

import androidx.annotation.Keep

@Keep
data class ManufacturerResponse(
    val active: Boolean,
    val orgId: String,
    val orgName: String,
    val orgType: String
)