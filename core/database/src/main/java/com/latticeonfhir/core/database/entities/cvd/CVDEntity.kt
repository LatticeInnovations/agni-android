package com.latticeonfhir.core.database.entities.cvd

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.ForeignKey
import com.latticeonfhir.core.database.entities.appointment.AppointmentEntity
import com.latticeonfhir.core.database.entities.patient.PatientEntity
import java.util.Date

@Keep
@Entity(
    primaryKeys = ["cvdUuid"],
    foreignKeys = [
        ForeignKey(
            entity = PatientEntity::class,
            parentColumns = ["id"],
            childColumns = ["patientId"]
        ),
        ForeignKey(
            entity = AppointmentEntity::class,
            parentColumns = ["id"],
            childColumns = ["appointmentId"]
        )
    ]
)
data class CVDEntity(
    val cvdFhirId: String?,
    val cvdUuid: String,
    val appointmentId: String,
    val patientId: String,
    val bmi: Double?,
    val bpDiastolic: Int,
    val bpSystolic: Int,
    val cholesterol: Double?,
    val cholesterolUnit: String?,
    val diabetic: Int,
    val heightCm: Double?,
    val heightFt: Int?,
    val heightInch: Double?,
    val practitionerName: String?,
    val risk: Int,
    val smoker: Int,
    val weight: Double?,
    val createdOn: Date
)