package com.latticeonfhir.core.data.local.repository.schedule

import com.latticeonfhir.core.data.local.roomdb.dao.ScheduleDao
import com.latticeonfhir.android.data.server.model.scheduleandappointment.schedule.ScheduleResponse
import com.latticeonfhir.core.utils.converters.responseconverter.toScheduleEntity
import com.latticeonfhir.core.utils.converters.responseconverter.toScheduleResponse
import javax.inject.Inject

class ScheduleRepositoryImpl @Inject constructor(private val scheduleDao: ScheduleDao) :
    ScheduleRepository {

    override suspend fun getBookedSlotsCount(startTime: Long): Int {
        return scheduleDao.getBookedSlotsCountByStartTime(startTime)
    }

    override suspend fun insertSchedule(scheduleResponse: ScheduleResponse): List<Long> {
        return scheduleDao.insertScheduleEntity(scheduleResponse.toScheduleEntity())
    }

    override suspend fun getScheduleById(id: String): ScheduleResponse {
        return scheduleDao.getScheduleById(id)[0].toScheduleResponse()
    }

    override suspend fun updateSchedule(scheduleResponse: ScheduleResponse): Int {
        return scheduleDao.updateScheduleEntity(scheduleResponse.toScheduleEntity())
    }

    override suspend fun getScheduleByStartTime(startTime: Long): ScheduleResponse? {
        return scheduleDao.getScheduleByStartTime(startTime)?.toScheduleResponse()
    }
}