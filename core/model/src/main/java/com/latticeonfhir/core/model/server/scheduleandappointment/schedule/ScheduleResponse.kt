package com.latticeonfhir.core.model.server.scheduleandappointment.schedule

import android.os.Parcelable
import androidx.annotation.Keep
import com.latticeonfhir.core.model.server.scheduleandappointment.Slot
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class ScheduleResponse(
    val uuid: String,
    val scheduleId: String?,
    val planningHorizon: Slot,
    val orgId: String,
    val bookedSlots: Int?
) : Parcelable
