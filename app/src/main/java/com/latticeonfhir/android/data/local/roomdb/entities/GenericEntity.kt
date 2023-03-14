package com.latticeonfhir.android.data.local.roomdb.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.latticeonfhir.android.data.local.enums.GenericTypeEnum

@Entity(indices = [Index("patientId")])
data class GenericEntity(
    @PrimaryKey
    val id: String,
    val patientId: String,
    val payload: String,
    val type: GenericTypeEnum
)