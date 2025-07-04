package com.latticeonfhir.android.data.local.roomdb.entities.medication

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Keep
@Entity(
    indices = [Index("id"), Index("medFhirId")],
    primaryKeys = ["id"],
    foreignKeys = [
        ForeignKey(
            entity = MedicationEntity::class,
            parentColumns = ["medFhirId"],
            childColumns = ["medFhirId"]
        )
    ]
)
data class StrengthEntity (
    val id: String,
    val medFhirId: String,
    val medName: String,
    val unitMeasureValue: String,
    val medMeasureCode: String
)