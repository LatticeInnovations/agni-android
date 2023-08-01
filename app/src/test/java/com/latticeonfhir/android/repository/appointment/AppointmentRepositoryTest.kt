package com.latticeonfhir.android.repository.appointment

import com.latticeonfhir.android.base.BaseClass
import com.latticeonfhir.android.data.local.enums.AppointmentStatusEnum
import com.latticeonfhir.android.data.local.repository.appointment.AppointmentRepositoryImpl
import com.latticeonfhir.android.data.local.roomdb.dao.AppointmentDao
import com.latticeonfhir.android.utils.converters.responseconverter.toAppointmentEntity
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@RunWith(JUnit4::class)
class AppointmentRepositoryTest: BaseClass() {
    @Mock
    private lateinit var appointmentDao: AppointmentDao
    lateinit var appointmentRepositoryImpl: AppointmentRepositoryImpl

    @Before
    public override fun setUp() {
        MockitoAnnotations.openMocks(this)
        appointmentRepositoryImpl = AppointmentRepositoryImpl(appointmentDao)
    }

    @Test
    fun getAppointmentListByDateTest() = runBlocking{
        `when`(appointmentDao.getAppointmentsByDate(date, date)).thenReturn(listOf(appointmentResponse.toAppointmentEntity()))
        val result = appointmentRepositoryImpl.getAppointmentListByDate(date, date)
        assertEquals(listOf(appointmentResponse.toAppointmentEntity()), result)
    }

    @Test
    fun addAppointmentTest() = runBlocking {
        `when`(appointmentDao.insertAppointmentEntity(appointmentResponse.toAppointmentEntity())).thenReturn(
            listOf(-1L)
        )
        val result = appointmentRepositoryImpl.addAppointment(appointmentResponse)
        assertEquals(listOf(-1L), result)
    }

    @Test
    fun updateAppointmentTest() = runBlocking {
        `when`(appointmentDao.updateAppointmentEntity(appointmentResponse.toAppointmentEntity())).thenReturn(
            1
        )
        val result = appointmentRepositoryImpl.updateAppointment(appointmentResponse)
        assertEquals(1, result)
    }

    @Test
    fun getAppointmentsOfPatientByStatusTest() = runBlocking {
        `when`(appointmentDao.getAppointmentsOfPatientByStatus(id, AppointmentStatusEnum.NO_SHOW.value)).thenReturn(
            listOf(appointmentResponse.toAppointmentEntity())
        )
        val result = appointmentRepositoryImpl.getAppointmentsOfPatientByStatus(id,
            AppointmentStatusEnum.NO_SHOW.value
        )
        assertEquals(listOf(appointmentResponse.toAppointmentEntity()), result)
    }
}