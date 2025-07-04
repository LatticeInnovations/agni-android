package com.heartcare.agni.data.local.model.search

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class SearchParameters(
    val patientId: String?,
    val name: String?,
    val minAge: Int?,
    val maxAge: Int?,
    val gender: String?,
    val lastFacilityVisit: String?,
    val addressLine1: String?,
    val city: String?,
    val district: String?,
    val state: String?,
    val postalCode: String?,
    val addressLine2: String?
) : Parcelable
