package com.heartcare.agni.data.local.roomdb.views

import androidx.annotation.Keep
import androidx.room.DatabaseView
import androidx.room.Embedded
import com.heartcare.agni.data.local.roomdb.entities.medication.MedicationEntity
import com.heartcare.agni.data.local.roomdb.entities.prescription.PrescriptionDirectionsEntity

@Keep
@DatabaseView("SELECT * FROM PrescriptionDirectionsEntity prescriptionDirectionsEntity INNER JOIN MedicationEntity medicationEntity ON medicationEntity.medFhirId = prescriptionDirectionsEntity.med_fhir_id")
data class PrescriptionDirectionAndMedicineView(
    @Embedded val prescriptionDirectionsEntity: PrescriptionDirectionsEntity,
    @Embedded val medicationEntity: MedicationEntity
)
