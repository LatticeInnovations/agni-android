package com.latticeonfhir.android.data.local.roomdb.entities.patient

import androidx.annotation.Keep

@Keep
data class PermanentAddressEntity(
    val state: String,
    val district: String,
    val block: String?,
    val city: String?,
    val addressLine1: String?,
    val addressLine2: String?,
    val country: String?,
    val postalCode: String?
)