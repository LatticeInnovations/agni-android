package com.heartcare.agni.data.local.model.search

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class SearchParameters(
    val name: String?,
    val fhirId: String?,
    val riskCategory: List<String>?,
    val heartcareId: String?,
    val hospitalId: String?,
    val nationalId: String?,
    val provinceId: String?,
    val areaCouncilId: String?,
    val minAge: Int?,
    val maxAge: Int?,
    val gender: String?,
    val lastFacilityVisit: String?
) : Parcelable
