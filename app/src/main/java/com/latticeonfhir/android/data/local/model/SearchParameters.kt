package com.latticeonfhir.android.data.local.model

import androidx.annotation.Keep

@Keep
data class SearchParameters(
    val patientId: String?,
    val name: String?,
    val minAge: Int?,
    val maxAge: Int?,
    val lastFacilityVisit: String?,
    val addressLine1: String?,
    val city: String?,
    val district: String?,
    val state: String?,
    val postalCode: String?,
    val country: String?,
    val addressLine2: String?
)
