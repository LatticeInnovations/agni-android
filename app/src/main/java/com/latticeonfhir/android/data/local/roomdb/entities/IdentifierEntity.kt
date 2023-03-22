package com.latticeonfhir.android.data.local.roomdb.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [Index("patientId")],
    foreignKeys = [ForeignKey(
        entity = PatientEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("patientId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class IdentifierEntity(
    @PrimaryKey
    val identifierNumber: String,
    val identifierType: String,
    val identifierCode: String?,
    val patientId: String
)