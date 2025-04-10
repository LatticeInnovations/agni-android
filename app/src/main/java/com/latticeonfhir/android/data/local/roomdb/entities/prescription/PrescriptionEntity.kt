package com.latticeonfhir.core.data.local.roomdb.entities.prescription

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.latticeonfhir.core.data.local.roomdb.entities.patient.PatientEntity
import java.util.Date

@Keep
@Entity(
    indices = [Index("patientId"), Index("patientFhirId"), Index("appointmentId")],
    foreignKeys = [
        ForeignKey(
            entity = PatientEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("patientId")
        )
    ]
)
data class PrescriptionEntity(
    @PrimaryKey val id: String,
    val prescriptionDate: Date,
    @ColumnInfo(defaultValue = "DEFAULT_APPOINTMENT_ID")
    val appointmentId: String,
    val patientId: String,
    val patientFhirId: String?,
    val prescriptionFhirId: String?,
    @ColumnInfo(defaultValue = "DEFAULT_PRESCRIPTION_TYPE")
    val prescriptionType: String,
)
