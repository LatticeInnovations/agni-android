package com.latticeonfhir.core.database.entities.prescription

import androidx.annotation.Keep
import androidx.room.Embedded
import androidx.room.Relation
import com.latticeonfhir.core.database.views.PrescriptionDirectionAndMedicineView

@Keep
data class PrescriptionAndMedicineRelation(
    @Embedded val prescriptionEntity: PrescriptionEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "prescriptionId"
    ) val prescriptionDirectionAndMedicineView: List<PrescriptionDirectionAndMedicineView>
)
