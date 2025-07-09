package com.heartcare.agni.data.server.model.levels

import androidx.annotation.Keep

@Keep
data class LevelResponse(
    val fhirId: String,
    val code: String,
    val levelType: String,
    val name: String,
    val population: Int?,
    val precedingLevelId: String?,
    val secondaryName: String?,
    val status: String
)