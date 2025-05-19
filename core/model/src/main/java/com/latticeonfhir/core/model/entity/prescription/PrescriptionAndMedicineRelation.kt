package com.latticeonfhir.core.model.entity.prescription

import androidx.annotation.Keep
import androidx.room.Embedded
import androidx.room.Relation

@Keep
data class PrescriptionAndMedicineRelation(
    @Embedded val prescriptionEntity: PrescriptionEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "prescriptionId"
    ) val prescriptionDirectionAndMedicineView: List<PrescriptionDirectionAndMedicineView>
)
