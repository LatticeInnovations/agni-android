package com.latticeonfhir.android.data.local.repository.schedule

import java.util.Date

interface ScheduleRepository {
    suspend fun getBookedSlotsCount(startTime: Date): Int
}