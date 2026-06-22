package com.latticeonfhir.android.data.server.model.patient

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class PatientAddressResponse(
    val state: String,
    val district: String,
    val block: String?,
    val city: String?,
    val addressLine1: String?,
    val addressLine2: String?,
    val country: String?,
    val postalCode: String?
) : Parcelable
