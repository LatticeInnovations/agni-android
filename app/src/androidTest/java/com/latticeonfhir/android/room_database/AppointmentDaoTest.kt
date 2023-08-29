package com.latticeonfhir.android.room_database

import com.latticeonfhir.android.data.local.enums.AppointmentStatusEnum
import com.latticeonfhir.android.data.local.roomdb.entities.appointment.AppointmentEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toAppointmentEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toPatientEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toScheduleEntity
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class AppointmentDaoTest : FhirAppDatabaseTest() {
    @Before
    fun before() {
        runBlocking{
            patientDao.insertPatientData(patientResponse.toPatientEntity())
            scheduleDao.insertScheduleEntity(scheduleResponse.toScheduleEntity())
        }
    }

    @Test
    fun insertAppointmentEntityTest() = runBlocking {
        val result =
            appointmentDao.insertAppointmentEntity(appointmentResponseLocal.toAppointmentEntity())
        Assert.assertNotEquals("Appointment not inserted.", listOf<Long>(), result)
        assertEquals("On successful insertion, should return List<Long> of size 1.", 1, result.size)
    }

    @Test
    fun updateAppointmentFhirIdTest() = runBlocking {
        appointmentDao.insertAppointmentEntity(appointmentResponseLocal.toAppointmentEntity())
        val result = appointmentDao.updateAppointmentFhirId(
            appointmentResponseLocal.uuid,
            appointmentResponseLocal.appointmentId!!
        )
        assertEquals(1, result)
    }

    @Test
    fun getAppointmentsOfPatientByStatusTest() = runBlocking {
        appointmentDao.insertAppointmentEntity(appointmentResponseLocal.toAppointmentEntity())
        val result = appointmentDao.getAppointmentsOfPatientByStatus(appointmentResponseLocal.patientId, appointmentResponseLocal.status)
        assertEquals(listOf(appointmentResponseLocal.toAppointmentEntity()), result)
    }

    @Test
    fun getAppointmentsByDateTest() = runBlocking {
        appointmentDao.insertAppointmentEntity(appointmentResponseLocal.toAppointmentEntity())
        val result = appointmentDao.getAppointmentsByDate(date.time, date.time)
        assertEquals(listOf(appointmentResponseLocal.toAppointmentEntity()), result)
    }

    @Test
    fun updateAppointmentEntityTest() = runBlocking {
        appointmentDao.insertAppointmentEntity(appointmentResponseLocal.toAppointmentEntity())
        val result = appointmentDao.updateAppointmentEntity(appointmentResponseLocal.toAppointmentEntity())
        assertEquals(1, result)
    }

    @Test
    fun getTodayScheduledAppointmentsTest() = runBlocking {
        val result = appointmentDao.getTodayScheduledAppointments(AppointmentStatusEnum.NO_SHOW.value, date.time)
        assertEquals(emptyList<AppointmentEntity>(), result)
    }
}