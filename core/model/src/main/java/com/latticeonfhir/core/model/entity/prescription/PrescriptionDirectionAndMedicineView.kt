package com.latticeonfhir.core.model.entity.prescription

import androidx.annotation.Keep
import androidx.room.DatabaseView
import androidx.room.Embedded

@Keep
@DatabaseView("SELECT * FROM PrescriptionDirectionsEntity prescriptionDirectionsEntity INNER JOIN MedicationEntity medicationEntity ON medicationEntity.medFhirId = prescriptionDirectionsEntity.med_fhir_id")
data class PrescriptionDirectionAndMedicineView(
    @Embedded val prescriptionDirectionsEntity: PrescriptionDirectionsEntity,
    @Embedded val medicationEntity: MedicationEntity
)