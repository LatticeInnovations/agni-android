package com.latticeonfhir.android.data.local.roomdb.entities.medication

import androidx.room.Entity
import com.google.errorprone.annotations.Keep

@Keep
@Entity
data class MedicineDosageInstructionsEntity(
    val medicalDosage: String,
    val medicalDosageId: String
)
