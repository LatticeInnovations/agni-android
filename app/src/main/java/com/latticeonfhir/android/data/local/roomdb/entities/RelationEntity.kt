package com.latticeonfhir.android.data.local.roomdb.entities

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import com.latticeonfhir.android.data.local.enums.RelationEnum

@Keep
@Entity(
    indices = [Index("fromId", "toId")],
    foreignKeys = [ForeignKey(
        entity = PatientEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("fromId")
    )]
)
data class RelationEntity(
    @PrimaryKey
    val id: String,
    val fromId: String,
    val toId: String,
    val relation: RelationEnum
)