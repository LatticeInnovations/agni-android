package com.latticeonfhir.android.data.local.roomdb.entities.patient

import androidx.annotation.Keep

@Keep
data class PermanentAddressEntity(
    val addressLine1: String,
    val city: String,
    val district: String?,
    val state: String,
    val postalCode: String,
    val country: String?,
    val addressLine2: String?
)