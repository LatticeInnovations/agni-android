package com.latticeonfhir.android.data.local.roomdb.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.latticeonfhir.android.data.local.enums.RelationEnum

@Entity(
    indices = [Index("fromId", "toId")],
    foreignKeys = [ForeignKey(
        entity = PatientEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("fromId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class RelationEntity(
    @PrimaryKey
    val id: String,
    val fromId: String,
    val toId: String,
    val relation: RelationEnum
)