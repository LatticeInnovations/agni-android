package com.latticeonfhir.android.data.local.roomdb.entities.vaccination

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.latticeonfhir.android.data.local.roomdb.entities.patient.PatientEntity
import java.util.Date

@Keep
@Entity(
    indices = [Index("patientId"), Index("vaccineCode"), Index("immunizationFhirId"), Index("appointmentId")],
    foreignKeys = [
        ForeignKey(
            entity = PatientEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("patientId")
        )
    ]
)
data class ImmunizationEntity(
    @PrimaryKey
    val id: String,
    val appointmentId: String,
    val patientId: String,
    val createdOn: Date,
    val lotNumber: String,
    val expiryDate: Date,
    val manufacturerId: String?,
    val notes: String?,
    val vaccineCode: String,
    val immunizationFhirId: String?
)
