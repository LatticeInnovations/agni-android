package com.latticeonfhir.core.database.entities.relation

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.latticeonfhir.core.database.entities.patient.PatientEntity
import com.latticeonfhir.core.model.enums.RelationEnum

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