package com.latticeonfhir.android.data.local.roomdb.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index("personId")],
    foreignKeys = [ForeignKey(
        entity = PatientEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("personId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class IdentifierEntity(
    @PrimaryKey
    val identifierNumber: String,
    val identifierType: String,
    val identifierCode: String?,
    val personId: String
)