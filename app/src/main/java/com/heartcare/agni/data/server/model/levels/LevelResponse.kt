package com.heartcare.agni.data.server.model.levels

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class LevelResponse(
    val fhirId: String,
    val code: String,
    val levelType: String,
    val name: String,
    val population: Int?,
    val precedingLevelId: String?,
    val secondaryName: String?,
    val status: String
): Parcelable