package com.latticeonfhir.core.database.entities.dispense

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.latticeonfhir.core.database.entities.patient.PatientEntity
import com.latticeonfhir.core.database.entities.prescription.PrescriptionEntity

@Keep
@Entity(
    indices = [Index("patientId"), Index("prescriptionId")],
    primaryKeys = ["patientId", "prescriptionId"],
    foreignKeys = [
        ForeignKey(
            entity = PatientEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("patientId")
        ),
        ForeignKey(
            entity = PrescriptionEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("prescriptionId")
        )
    ]
)
data class DispensePrescriptionEntity(
    val patientId: String,
    val prescriptionId: String,
    val status: String
)