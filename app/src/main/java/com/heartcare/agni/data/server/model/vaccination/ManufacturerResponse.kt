package com.heartcare.agni.data.server.model.vaccination

import androidx.annotation.Keep

@Keep
data class ManufacturerResponse(
    val active: Boolean,
    val manufacturerId: String,
    val manufacturerName: String,
    val orgType: String
)