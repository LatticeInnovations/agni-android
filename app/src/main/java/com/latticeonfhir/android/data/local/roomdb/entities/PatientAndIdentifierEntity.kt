package com.latticeonfhir.android.data.local.roomdb.entities

import androidx.room.Embedded
import androidx.room.Relation

data class PatientAndIdentifierEntity(
    @Embedded val patientEntity: PatientEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "PatientId"
    ) val identifiers: List<IdentifierEntity>
)