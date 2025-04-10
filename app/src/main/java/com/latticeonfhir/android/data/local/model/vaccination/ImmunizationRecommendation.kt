package com.latticeonfhir.core.data.local.model.vaccination

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import java.util.Date

@Keep
@Parcelize
data class ImmunizationRecommendation(
    val id: String,
    val name: String,
    val shortName: String,
    val seriesDoses: Int,
    val doseNumber: Int,
    val vaccineStartDate: Date,
    val vaccineEndDate: Date,
    val takenOn: Date?,
    val vaccineCode: String,
    val vaccineDueDate: Date
) : Parcelable
