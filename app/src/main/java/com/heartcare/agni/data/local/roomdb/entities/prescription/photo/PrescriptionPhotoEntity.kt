package com.heartcare.agni.data.local.roomdb.entities.prescription.photo

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.heartcare.agni.data.local.roomdb.entities.prescription.PrescriptionEntity

@Keep
@Entity(
    indices = [Index("prescriptionId")],
    foreignKeys = [
        ForeignKey(
            entity = PrescriptionEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("prescriptionId")
        )
    ]
)
data class PrescriptionPhotoEntity(
    @PrimaryKey
    val id: String,
    val documentFhirId: String?,
    val prescriptionId: String,
    val fileName: String,
    val note: String?
)