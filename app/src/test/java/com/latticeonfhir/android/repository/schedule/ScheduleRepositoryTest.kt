package com.latticeonfhir.android.repository.schedule

import com.latticeonfhir.android.base.BaseClass
import com.latticeonfhir.android.data.local.repository.schedule.ScheduleRepositoryImpl
import com.latticeonfhir.android.data.local.roomdb.dao.ScheduleDao
import com.latticeonfhir.android.utils.converters.responseconverter.toScheduleEntity
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@RunWith(JUnit4::class)
class ScheduleRepositoryTest: BaseClass() {
    @Mock
    private lateinit var scheduleDao: ScheduleDao
    lateinit var scheduleRepositoryImpl: ScheduleRepositoryImpl

    @Before
    public override fun setUp() {
        MockitoAnnotations.openMocks(this)
        scheduleRepositoryImpl = ScheduleRepositoryImpl(scheduleDao)
    }

    @Test
    fun getBookedSlotsCountTest() = runBlocking {
        `when`(scheduleDao.getBookedSlotsCountByStartTime(date.time)).thenReturn(2)
        val result = scheduleRepositoryImpl.getBookedSlotsCount(date.time)
        assertEquals(2, result)
    }

    @Test
    fun updateScheduleTest() = runBlocking {
        `when`(scheduleDao.updateScheduleEntity(scheduleResponse.toScheduleEntity())).thenReturn(1)
        val result = scheduleRepositoryImpl.updateSchedule(scheduleResponse)
        assertEquals(1, result)
    }
    @Test
    fun insertScheduleTest() = runBlocking {
        `when`(scheduleDao.insertScheduleEntity(scheduleResponse.toScheduleEntity())).thenReturn(
            listOf(-1L)
        )
        val result = scheduleRepositoryImpl.insertSchedule(scheduleResponse)
        assertEquals(listOf(-1L), result)
    }

    @Test
    fun getScheduleByIdTest() = runBlocking {
        `when`(scheduleDao.getScheduleById(scheduleResponse.uuid)).thenReturn(
            listOf(scheduleResponse.toScheduleEntity())
        )
        val result = scheduleRepositoryImpl.getScheduleById(scheduleResponse.uuid)
        assertEquals(scheduleResponse, result)
    }

    @Test
    fun getScheduleByStartTimeTest() = runBlocking {
        `when`(scheduleDao.getScheduleByStartTime(date.time)).thenReturn(scheduleResponse.toScheduleEntity())
        val result = scheduleRepositoryImpl.getScheduleByStartTime(date.time)
        assertEquals(scheduleResponse, result)
    }
}