package com.latticeonfhir.android.data.local.roomdb.entities.prescription

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Keep
@Entity(
    indices = [Index("prescriptionId"), Index("med_fhir_id")],
    foreignKeys = [
        ForeignKey(
            entity = PrescriptionEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("prescriptionId")
        )
    ]
)
data class PrescriptionDirectionsEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo("med_fhir_id")
    val medFhirId: String,
    val qtyPerDose: Int,
    val frequency: Int,
    val timing: String?,
    val duration: Int,
    val qtyPrescribed: Int,
    val note: String?,
    val prescriptionId: String
)
