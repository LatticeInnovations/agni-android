package com.latticeonfhir.android.room_database

import com.latticeonfhir.android.utils.converters.responseconverter.toScheduleEntity
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotEquals
import org.junit.Test

class ScheduleDaoTest: FhirAppDatabaseTest() {

    @Test
    fun insertScheduleEntityTest() = runBlocking {
        val result = scheduleDao.insertScheduleEntity(scheduleResponse.toScheduleEntity())
        assertNotEquals("Schedule not inserted.", listOf<Long>(), result)
        assertEquals("On successful insertion, should return List<Long> of size 1.",1, result.size)
    }

    @Test
    fun updateScheduleFhirIdTest() = runBlocking {
        scheduleDao.insertScheduleEntity(scheduleResponse.toScheduleEntity())
        val result = scheduleDao.updateScheduleFhirId(scheduleResponse.uuid, scheduleResponse.scheduleId!!)
        assertEquals("schedule not updated.", 1, result)
    }

    @Test
    fun getScheduleByIdTest() = runBlocking {
        scheduleDao.insertScheduleEntity(scheduleResponse.toScheduleEntity())
        val result = scheduleDao.getScheduleById(scheduleResponse.uuid)
        assertEquals("did not return correct value", listOf(scheduleResponse.toScheduleEntity()), result)
    }

    @Test
    fun getScheduleByFhirIdTest() = runBlocking {
        scheduleDao.insertScheduleEntity(scheduleResponse.toScheduleEntity())
        val result = scheduleDao.getFhirIdByStartTime(scheduleResponse.planningHorizon.start)
        assertEquals(scheduleResponse.scheduleId, result)
    }

    @Test
    fun getBookedSlotsCountByStartTimeTest() = runBlocking {
        scheduleDao.insertScheduleEntity(scheduleResponse.toScheduleEntity())
        val result = scheduleDao.getBookedSlotsCountByStartTime(date.time)
        assertEquals(1, result)
    }

    @Test
    fun updateScheduleEntityTest() = runBlocking {
        scheduleDao.insertScheduleEntity(scheduleResponse.toScheduleEntity())
        val result = scheduleDao.updateScheduleEntity(scheduleResponse.toScheduleEntity())
        assertEquals(1, result)
    }
}