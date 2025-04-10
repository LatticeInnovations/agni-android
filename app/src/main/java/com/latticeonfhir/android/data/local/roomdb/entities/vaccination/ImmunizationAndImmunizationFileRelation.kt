package com.latticeonfhir.core.data.local.roomdb.entities.vaccination

import androidx.annotation.Keep
import androidx.room.Embedded
import androidx.room.Relation

@Keep
data class ImmunizationAndImmunizationFileRelation(
    @Embedded val immunizationEntity: ImmunizationEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "immunizationId"
    ) val immunizationFileEntities: List<ImmunizationFileEntity>
)