package com.latticeonfhir.core.database.entities.symptomsanddiagnosis

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "symptoms")
data class SymptomsEntity(
    @PrimaryKey
    val id: String,
    val code: String,
    val display: String,
    val type:String?=null,
    val gender:String?=null
)
