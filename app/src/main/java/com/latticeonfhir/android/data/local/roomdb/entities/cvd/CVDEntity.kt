package com.latticeonfhir.android.data.local.roomdb.entities.cvd

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.ForeignKey
import com.latticeonfhir.android.data.local.roomdb.entities.appointment.AppointmentEntity
import com.latticeonfhir.android.data.local.roomdb.entities.patient.PatientEntity
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
    val bmi: Int?,
    val bpDiastolic: Int,
    val bpSystolic: Int,
    val cholesterol: Int?,
    val cholesterolUnit: String?,
    val diabetic: Int,
    val heightCm: Int?,
    val heightFt: Int?,
    val heightInch: Int?,
    val practitionerName: String?,
    val risk: Int,
    val smoker: Int,
    val weight: Int?,
    val createdOn: Date
)