package com.latticeonfhir.android.data.local.roomdb.entities.prescription

import androidx.room.Embedded
import androidx.room.Relation
import com.google.errorprone.annotations.Keep
import com.latticeonfhir.android.data.local.roomdb.entities.medication.MedicationEntity

@Keep
data class PrescriptionDirectionAndMedicineRelation(
    @Embedded val prescriptionDirectionsEntity: PrescriptionDirectionsEntity,
    @Relation(
        parentColumn = "medFhirId",
        entityColumn = "medFhirId"
    ) val medicationEntity: MedicationEntity
)
