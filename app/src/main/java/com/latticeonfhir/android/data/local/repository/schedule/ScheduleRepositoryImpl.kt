package com.latticeonfhir.android.data.local.repository.schedule

import com.latticeonfhir.android.data.local.roomdb.dao.ScheduleDao
import com.latticeonfhir.android.data.server.model.scheduleandappointment.schedule.ScheduleResponse
import com.latticeonfhir.android.utils.converters.responseconverter.toScheduleEntity
import java.util.Date
import javax.inject.Inject

class ScheduleRepositoryImpl @Inject constructor(private val scheduleDao: ScheduleDao) :
    ScheduleRepository {

    override suspend fun getBookedSlotsCount(startTime: Date): Int {
        return scheduleDao.getBookedSlotsCountByStartTime(startTime)
    }

    override suspend fun updateSchedule(scheduleResponse: ScheduleResponse): Int {
        return scheduleDao.updateScheduleEntity(scheduleResponse.toScheduleEntity())
    }
}