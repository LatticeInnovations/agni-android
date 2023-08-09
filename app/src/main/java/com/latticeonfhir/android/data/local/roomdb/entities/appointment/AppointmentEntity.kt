package com.latticeonfhir.android.data.local.roomdb.entities.appointment

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.latticeonfhir.android.data.local.roomdb.entities.patient.PatientEntity
import com.latticeonfhir.android.data.local.roomdb.entities.schedule.ScheduleEntity
import java.util.Date

@Keep
@Entity(
    indices = [Index("patientId"), Index("scheduleId")],
    foreignKeys = [
        ForeignKey(
            entity = PatientEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("patientId")
        ),
        // To-be tested
        ForeignKey(
            entity = ScheduleEntity::class,
            parentColumns = arrayOf("startTime"),
            childColumns = arrayOf("scheduleId")
        )
    ]
)
data class AppointmentEntity(
    @PrimaryKey
    val id: String,
    val appointmentFhirId: String?,
    val patientId: String,
    val scheduleId: Date,
    val startTime: Date,
    val endTime: Date,
    val orgId: String,
    val createdOn: Date,
    val status: String
)
