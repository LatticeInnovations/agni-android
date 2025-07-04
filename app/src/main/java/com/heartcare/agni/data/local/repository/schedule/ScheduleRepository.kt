package com.heartcare.agni.data.local.repository.schedule

import com.heartcare.agni.data.server.model.scheduleandappointment.schedule.ScheduleResponse

interface ScheduleRepository {
    suspend fun getBookedSlotsCount(startTime: Long): Int
    suspend fun insertSchedule(scheduleResponse: ScheduleResponse): List<Long>
    suspend fun updateSchedule(scheduleResponse: ScheduleResponse): Int
    suspend fun getScheduleById(id: String): ScheduleResponse
    suspend fun getScheduleByStartTime(startTime: Long): ScheduleResponse?
}