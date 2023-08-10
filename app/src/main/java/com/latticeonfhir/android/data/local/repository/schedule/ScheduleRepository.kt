package com.latticeonfhir.android.data.local.repository.schedule

import com.latticeonfhir.android.data.server.model.scheduleandappointment.schedule.ScheduleResponse

interface ScheduleRepository {
    suspend fun getBookedSlotsCount(startTime: Long): Int
    suspend fun insertSchedule(scheduleResponse: ScheduleResponse): List<Long>
    suspend fun updateSchedule(scheduleResponse: ScheduleResponse): Int
    suspend fun getScheduleById(id: String): ScheduleResponse
    suspend fun getScheduleByStartTime(startTime: Long): ScheduleResponse?
}