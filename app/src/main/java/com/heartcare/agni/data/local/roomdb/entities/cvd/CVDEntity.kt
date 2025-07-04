package com.heartcare.agni.data.local.roomdb.entities.cvd

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.heartcare.agni.data.local.roomdb.entities.appointment.AppointmentEntity
import com.heartcare.agni.data.local.roomdb.entities.patient.PatientEntity
import java.util.Date

@Keep
@Entity(
    indices = [Index("patientId"), Index("appointmentId")],
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