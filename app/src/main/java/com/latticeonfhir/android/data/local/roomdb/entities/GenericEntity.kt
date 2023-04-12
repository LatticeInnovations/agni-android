package com.latticeonfhir.android.data.local.roomdb.entities

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.latticeonfhir.android.data.local.enums.GenericTypeEnum
import com.latticeonfhir.android.data.local.enums.SyncType

@Keep
@Entity(indices = [Index("patientId")])
data class GenericEntity(
    @PrimaryKey
    val id: String,
    val patientId: String,
    val payload: String,
    val type: GenericTypeEnum,
    val syncType: SyncType
)