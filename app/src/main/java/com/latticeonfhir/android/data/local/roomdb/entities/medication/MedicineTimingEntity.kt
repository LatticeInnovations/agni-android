package com.latticeonfhir.android.data.local.roomdb.entities.medication

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity
data class MedicineTimingEntity(
    @PrimaryKey
    val medicalDosageId: String,
    val medicalDosage: String
)
