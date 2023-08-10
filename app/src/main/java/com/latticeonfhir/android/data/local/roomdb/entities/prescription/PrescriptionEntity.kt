package com.latticeonfhir.android.data.local.roomdb.entities.prescription

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.latticeonfhir.android.data.local.roomdb.entities.appointment.AppointmentEntity
import com.latticeonfhir.android.data.local.roomdb.entities.patient.PatientEntity
import java.util.Date

@Keep
@Entity(
    indices = [Index("patientId"), Index("patientFhirId"), Index("appointmentId")],
    foreignKeys = [
        ForeignKey(
            entity = PatientEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("patientId")
        ),
        ForeignKey(
            entity = AppointmentEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("appointmentId")
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
    val prescriptionFhirId: String?
)
