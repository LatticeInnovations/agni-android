package com.latticeonfhir.android.data.local.roomdb.entities.dispense

import androidx.annotation.Keep
import androidx.room.Embedded
import androidx.room.Relation
import com.latticeonfhir.android.data.local.roomdb.entities.prescription.PrescriptionEntity

@Keep
data class DispensedPrescriptionInfo(
    @Embedded val dispenseDataEntity: DispenseDataEntity,
    @Relation(
        parentColumn = "prescriptionId",
        entityColumn = "id"
    ) val prescription: PrescriptionEntity?,
    @Relation(
        parentColumn = "dispenseId",
        entityColumn = "dispenseId"
    ) val medicineDispenseList: List<MedicineDispenseListEntity>
)
