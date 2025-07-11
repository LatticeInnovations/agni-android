package com.heartcare.agni.data.local.roomdb.entities.levels

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity
data class LevelEntity(
    @PrimaryKey
    val fhirId: String,
    val code: String,
    val levelType: String,
    val name: String,
    val population: Int?,
    val precedingLevelId: String?,
    val secondaryName: String?,
    val status: String
)
