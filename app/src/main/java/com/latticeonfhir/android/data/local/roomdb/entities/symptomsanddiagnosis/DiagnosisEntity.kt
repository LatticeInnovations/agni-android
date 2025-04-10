package com.latticeonfhir.core.data.local.roomdb.entities.symptomsanddiagnosis

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "diagnosis")
data class DiagnosisEntity(
    val id: String, @PrimaryKey val code: String, val display: String
)
