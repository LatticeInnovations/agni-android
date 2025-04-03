package com.latticeonfhir.core.database.entities.prescription.photo

import androidx.annotation.Keep
import androidx.room.Embedded
import androidx.room.Relation
import com.latticeonfhir.core.database.entities.prescription.PrescriptionEntity

@Keep
data class PrescriptionAndFileEntity(
    @Embedded val prescriptionEntity: PrescriptionEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "prescriptionId"
    ) val prescriptionPhotoEntity: List<PrescriptionPhotoEntity>
)