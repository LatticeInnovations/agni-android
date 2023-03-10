package com.latticeonfhir.android.data.local.roomdb.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.server.model.PatientResponse

@Entity
data class GenericEntity(
    @PrimaryKey
    val id: String,
    val payload: String,
    val type: GenericTypeEnum
)