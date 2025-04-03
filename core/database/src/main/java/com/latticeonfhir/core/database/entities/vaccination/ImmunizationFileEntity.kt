package com.latticeonfhir.core.database.entities.vaccination

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Keep
@Entity(
    indices = [Index("immunizationId")],
    foreignKeys = [
        ForeignKey(
            entity = ImmunizationEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("immunizationId")
        )
    ]
)
data class ImmunizationFileEntity(
    @PrimaryKey
    val filename: String,
    val immunizationId: String
)
