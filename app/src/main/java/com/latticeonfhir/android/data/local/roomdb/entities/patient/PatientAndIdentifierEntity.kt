package com.latticeonfhir.android.data.local.roomdb.entities.patient

import androidx.annotation.Keep
import androidx.room.Embedded
import androidx.room.Relation

@Keep
data class PatientAndIdentifierEntity(
    @Embedded val patientEntity: PatientEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "patientId"
    ) val identifiers: List<IdentifierEntity>
)