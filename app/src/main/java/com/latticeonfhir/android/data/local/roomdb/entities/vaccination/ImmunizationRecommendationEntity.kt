package com.latticeonfhir.android.data.local.roomdb.entities.vaccination

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Keep
@Entity(indices = [Index("patientFhirId")])
data class ImmunizationRecommendationEntity(
    @PrimaryKey
    val id: String,
    val patientFhirId: String,
    val vaccine: String,
    val vaccineShortName: String,
    val vaccineCode: String,
    val seriesDoses: Int,
    val doseNumber: Int,
    val vaccineStartDate: Date,
    val vaccineEndDate: Date
)
