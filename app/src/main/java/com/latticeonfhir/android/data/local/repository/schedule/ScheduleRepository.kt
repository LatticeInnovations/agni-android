package com.latticeonfhir.android.data.local.repository.schedule

import com.latticeonfhir.android.data.server.model.scheduleandappointment.schedule.ScheduleResponse
import java.util.Date

interface ScheduleRepository {
    suspend fun getBookedSlotsCount(startTime: Date): Int
    suspend fun updateSchedule(scheduleResponse: ScheduleResponse): Int
}