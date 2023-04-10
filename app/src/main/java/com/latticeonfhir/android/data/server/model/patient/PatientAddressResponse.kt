package com.latticeonfhir.android.data.server.model.patient

import androidx.annotation.Keep
import com.latticeonfhir.android.base.baseclass.ParcelableClass

@Keep
data class PatientAddressResponse(
    val addressLine1: String,
    val city: String?,
    val district: String?,
    val state: String,
    val postalCode: String,
    val country: String?,
    val addressLine2: String?
): ParcelableClass()
