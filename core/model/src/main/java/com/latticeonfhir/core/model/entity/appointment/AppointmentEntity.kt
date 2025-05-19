package com.latticeonfhir.core.model.entity.appointment

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.latticeonfhir.core.model.entity.patient.PatientEntity
import com.latticeonfhir.core.model.entity.schedule.ScheduleEntity
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
    val status: String,
    @ColumnInfo(defaultValue = "DEFAULT_APPOINTMENT_TYPE")
    val appointmentType: String,
    val inProgressTime: Date?
)
