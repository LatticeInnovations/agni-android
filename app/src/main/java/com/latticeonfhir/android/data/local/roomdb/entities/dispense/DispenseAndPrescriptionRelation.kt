package com.latticeonfhir.core.data.local.roomdb.entities.dispense

import androidx.annotation.Keep
import androidx.room.Embedded
import androidx.room.Relation
import com.latticeonfhir.core.data.local.roomdb.entities.prescription.PrescriptionEntity
import com.latticeonfhir.core.data.local.roomdb.views.PrescriptionDirectionAndMedicineView

@Keep
data class DispenseAndPrescriptionRelation(
    @Embedded val dispensePrescriptionEntity: DispensePrescriptionEntity,
    @Relation(
        parentColumn = "prescriptionId",
        entityColumn = "id"
    ) val prescription: PrescriptionEntity,
    @Relation(
        parentColumn = "prescriptionId",
        entityColumn = "prescriptionId"
    ) val prescriptionDirectionAndMedicineView: List<PrescriptionDirectionAndMedicineView>
)