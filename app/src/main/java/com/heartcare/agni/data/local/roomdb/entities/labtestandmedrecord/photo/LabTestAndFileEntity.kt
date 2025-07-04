package com.heartcare.agni.data.local.roomdb.entities.labtestandmedrecord.photo

import androidx.annotation.Keep
import androidx.room.Embedded
import androidx.room.Relation
import com.heartcare.agni.data.local.roomdb.entities.labtestandmedrecord.LabTestAndMedEntity

@Keep
data class LabTestAndFileEntity(
    @Embedded val labTestAndMedEntity: LabTestAndMedEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "labTestId"
    ) val labTestAndMedPhotoEntity: List<LabTestAndMedPhotoEntity>
)