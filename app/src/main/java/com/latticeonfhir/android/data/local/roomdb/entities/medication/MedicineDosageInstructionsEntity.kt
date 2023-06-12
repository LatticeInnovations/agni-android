package com.latticeonfhir.android.data.local.roomdb.entities.medication

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.annotation.Keep

@Keep
@Entity
data class MedicineDosageInstructionsEntity(
    val medicalDosage: String,
    val medicalDosageId: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
