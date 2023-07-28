package com.latticeonfhir.android.data.local.repository.schedule

import com.latticeonfhir.android.data.local.roomdb.dao.ScheduleDao
import java.util.Date
import javax.inject.Inject

class ScheduleRepositoryImpl @Inject constructor(private val scheduleDao: ScheduleDao) :
    ScheduleRepository {

    override suspend fun getBookedSlotsCount(startTime: Date): Int {
        return scheduleDao.getBookedSlotsCountByStartTime(startTime)
    }

}