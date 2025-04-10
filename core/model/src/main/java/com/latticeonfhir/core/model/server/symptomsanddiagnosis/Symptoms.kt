package com.latticeonfhir.core.data.server.model.symptomsanddiagnosis

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
data class Symptoms(
    val symptoms: List<SymptomsItem>
)

@Keep
@Parcelize
data class SymptomsItem(
    val code: String,
    val display: String,
    val type: String?,
    val gender: String?
) : Parcelable

@Keep
@Parcelize
data class SymptomsAndDiagnosisItem(
    val code: String,
    val display: String
) : Parcelable