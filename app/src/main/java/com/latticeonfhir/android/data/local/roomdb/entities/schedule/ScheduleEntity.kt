package com.latticeonfhir.android.data.local.roomdb.entities.schedule

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Keep
@Entity
data class ScheduleEntity (
    @PrimaryKey
    val id: String,
    val scheduleFhirId: String?,
    val startTime: Date,
    val endTime: Date,
    val orgId: String,
    val bookedSlots: Int
)