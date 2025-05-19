package com.latticeonfhir.core.model.entity.vaccination

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity
data class ManufacturerEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val type: String,
    val active: Boolean
)
