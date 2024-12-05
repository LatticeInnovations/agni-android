package com.latticeonfhir.android.data.local.roomdb.entities.labtestandmedrecord

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.latticeonfhir.android.data.local.roomdb.entities.patient.PatientEntity
import java.util.Date

@Keep
@Entity(
    indices = [
        Index("patientId"),
        Index("labTestFhirId"),
        Index("appointmentId")
    ],
    foreignKeys = [ForeignKey(
        entity = PatientEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("patientId")
    )]
)
data class LabTestAndMedEntity(
    @PrimaryKey
    val id: String,
    val appointmentId: String,
    val labTestFhirId: String?,
    val patientId: String,
    val createdOn: Date,
    val type: String = ""
)