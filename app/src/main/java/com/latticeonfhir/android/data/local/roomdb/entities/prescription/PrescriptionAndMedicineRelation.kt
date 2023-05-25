package com.latticeonfhir.android.data.local.roomdb.entities.prescription

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation
import com.google.errorprone.annotations.Keep

@Keep
data class PrescriptionAndMedicineRelation(
    @Embedded val prescriptionEntity: PrescriptionEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "prescriptionId"
    ) val prescriptionDirectionAndMedicineRelation: List<PrescriptionDirectionAndMedicineRelation>
)
