package com.latticeonfhir.android.data.server.model.patient

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class PatientAddressResponse(
    val addressLine1: String,
    val city: String,
    val district: String?,
    val state: String,
    val postalCode: String,
    val country: String?,
    val addressLine2: String?
) : Parcelable
