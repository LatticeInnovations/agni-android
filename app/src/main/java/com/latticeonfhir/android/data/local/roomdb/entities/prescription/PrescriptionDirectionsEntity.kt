package com.latticeonfhir.android.data.local.roomdb.entities.prescription

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.latticeonfhir.android.data.local.roomdb.entities.medication.MedicationEntity

@Keep
@Entity(
    foreignKeys = [
        ForeignKey(
            entity = PrescriptionEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("prescriptionId")
        ),
        ForeignKey(
            entity = MedicationEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("medFhirId")
        )
    ]
)
data class PrescriptionDirectionsEntity(
    val medFhirId: String,
    val qtyPerDose: Int,
    val dosageInstruction: String,
    val duration: Int,
    val qtyPrescribed: Int,
    val note: String,
    val prescriptionId: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
