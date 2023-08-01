package com.latticeonfhir.android.data.local.repository.appointment

import com.latticeonfhir.android.data.local.roomdb.dao.AppointmentDao
import com.latticeonfhir.android.data.local.roomdb.entities.appointment.AppointmentEntity
import com.latticeonfhir.android.data.server.model.scheduleandappointment.appointment.AppointmentResponse
import com.latticeonfhir.android.utils.converters.responseconverter.toAppointmentEntity
import java.util.Date
import javax.inject.Inject

class AppointmentRepositoryImpl @Inject constructor(private val appointmentDao: AppointmentDao) :
    AppointmentRepository {

    override suspend fun getAppointmentListByDate(startOfDay: Date, endOfDay: Date): List<AppointmentEntity>{
        return appointmentDao.getAppointmentsByDate(startOfDay, endOfDay)
    }

    override suspend fun addAppointment(appointmentResponse: AppointmentResponse): List<Long> {
        return appointmentDao.insertAppointmentEntity(appointmentResponse.toAppointmentEntity())
    }

    override suspend fun updateAppointment(appointmentResponse: AppointmentResponse): Int {
        return appointmentDao.updateAppointmentEntity(appointmentResponse.toAppointmentEntity())
    }

    override suspend fun getAppointmentsOfPatientByStatus(patientId: String, status: String): List<AppointmentEntity> {
        return appointmentDao.getAppointmentsOfPatientByStatus(patientId, status)
    }
}