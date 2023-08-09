package com.latticeonfhir.android.data.server.model.scheduleandappointment.schedule

import android.os.Parcelable
import androidx.annotation.Keep
import com.latticeonfhir.android.data.server.model.scheduleandappointment.Slot
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class ScheduleResponse(
    val uuid: String,
    val scheduleId: String?,
    val planningHorizon: Slot,
    val orgId: String,
    val bookedSlots: Int?
): Parcelable
