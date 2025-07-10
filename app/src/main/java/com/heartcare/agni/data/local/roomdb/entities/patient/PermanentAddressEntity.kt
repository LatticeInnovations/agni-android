package com.heartcare.agni.data.local.roomdb.entities.patient

import androidx.annotation.Keep

@Keep
data class PermanentAddressEntity(
    val village: String?,
    val addressLine2: String?,
    val areaCouncil: String,
    val country: String,
    val island: String,
    val postalCode: String?,
    val province: String
)