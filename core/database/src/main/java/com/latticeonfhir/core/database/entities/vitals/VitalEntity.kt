package com.latticeonfhir.core.database.entities.vitals

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.latticeonfhir.core.database.entities.patient.PatientEntity
import java.util.Date

@Keep
@Entity(
    indices = [Index("fhirId"), Index("patientId"), Index("appointmentId")],
    foreignKeys = [ForeignKey(
        entity = PatientEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("patientId")
    )]
)
data class VitalEntity(
    @PrimaryKey
    val vitalUuid: String,
    val fhirId: String?,
    val patientId: String?,
    val appointmentId: String,
    val bloodGlucose: String?,
    val bloodGlucoseType: String?,
    val bloodGlucoseUnit: String?,
    val bpDiastolic: String?,
    val bpSystolic: String?,
    val createdOn: Date,
    val eyeTestType: String?,
    val heartRate: String?,
    val heightCm: String?,
    val heightFt: String?,
    val heightInch: String?,
    val leftEye: Int?,
    val respRate: String?,
    val rightEye: Int?,
    val spo2: String?,
    val temp: String?,
    val tempUnit: String?,
    val weight: String?,
    val practitionerName: String?,
    val cholesterol: Double?,
    val cholesterolUnit: String?,
)
