package com.latticeonfhir.android.data.local.roomdb.entities

import androidx.room.Embedded
import androidx.room.Relation

data class PersonAndIdentifierEntity(
    @Embedded val patientEntity: PatientEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "personId"
    ) val identifiers: List<IdentifierEntity>
)