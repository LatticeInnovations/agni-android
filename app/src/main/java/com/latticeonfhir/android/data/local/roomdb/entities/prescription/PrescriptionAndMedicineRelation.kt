package com.latticeonfhir.android.data.local.roomdb.entities.prescription

import androidx.room.Embedded
import androidx.room.Relation
import androidx.annotation.Keep
import com.latticeonfhir.android.data.local.roomdb.views.PrescriptionDirectionAndMedicineView

@Keep
data class PrescriptionAndMedicineRelation(
    @Embedded val prescriptionEntity: PrescriptionEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "prescriptionId"
    ) val prescriptionDirectionAndMedicineView: List<PrescriptionDirectionAndMedicineView>
)
