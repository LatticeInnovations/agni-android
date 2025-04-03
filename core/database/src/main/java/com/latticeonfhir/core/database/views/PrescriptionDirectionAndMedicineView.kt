package com.latticeonfhir.core.database.views

import androidx.annotation.Keep
import androidx.room.DatabaseView
import androidx.room.Embedded
import com.latticeonfhir.core.database.entities.medication.MedicationEntity
import com.latticeonfhir.core.database.entities.prescription.PrescriptionDirectionsEntity

@Keep
@DatabaseView("SELECT * FROM PrescriptionDirectionsEntity prescriptionDirectionsEntity INNER JOIN MedicationEntity medicationEntity ON medicationEntity.medFhirId = prescriptionDirectionsEntity.med_fhir_id")
data class PrescriptionDirectionAndMedicineView(
    @Embedded val prescriptionDirectionsEntity: PrescriptionDirectionsEntity,
    @Embedded val medicationEntity: MedicationEntity
)
