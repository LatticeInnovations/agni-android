package com.latticeonfhir.android.data.local.roomdb.entities.medication

import androidx.annotation.Keep
import androidx.room.Embedded
import androidx.room.Relation

@Keep
data class MedicationStrengthRelation(
    @Embedded val medicationEntity: MedicationEntity,
    @Relation(
        parentColumn = "medFhirId",
        entityColumn = "medFhirId"
    ) val strength: List<StrengthEntity>
)