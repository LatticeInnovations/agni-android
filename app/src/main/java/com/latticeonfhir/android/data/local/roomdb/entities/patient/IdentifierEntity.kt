package com.latticeonfhir.android.data.local.roomdb.entities.patient

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Keep
@Entity(
    indices = [Index("patientId")],
    foreignKeys = [ForeignKey(
        entity = PatientEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("patientId")
    )]
)
data class IdentifierEntity(
    @PrimaryKey
    val identifierNumber: String,
    val identifierType: String,
    val identifierCode: String?,
    val patientId: String
)