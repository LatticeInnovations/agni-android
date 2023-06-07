package com.latticeonfhir.android.data.local.roomdb.views

import androidx.room.DatabaseView
import androidx.room.Embedded
import com.google.errorprone.annotations.Keep
import com.latticeonfhir.android.data.local.roomdb.entities.medication.MedicationEntity
import com.latticeonfhir.android.data.local.roomdb.entities.prescription.PrescriptionDirectionsEntity

@Keep
@DatabaseView("SELECT * FROM PrescriptionDirectionsEntity prescriptionDirectionsEntity INNER JOIN MedicationEntity medicationEntity ON medicationEntity.medFhirId = prescriptionDirectionsEntity.med_fhir_id")
data class PrescriptionDirectionAndMedicineView(
    @Embedded val prescriptionDirectionsEntity: PrescriptionDirectionsEntity,
    @Embedded val medicationEntity: MedicationEntity
)
