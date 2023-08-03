package com.latticeonfhir.android.data.local.repository.appointment

import com.latticeonfhir.android.data.local.roomdb.dao.AppointmentDao
import com.latticeonfhir.android.data.server.model.scheduleandappointment.appointment.AppointmentResponse
import com.latticeonfhir.android.utils.converters.responseconverter.toAppointmentEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toAppointmentResponse
import javax.inject.Inject

class AppointmentRepositoryImpl @Inject constructor(private val appointmentDao: AppointmentDao) :
    AppointmentRepository {

    override suspend fun getAppointmentListByDate(
        startOfDay: Long,
        endOfDay: Long
    ): List<AppointmentResponse> {
        return appointmentDao.getAppointmentsByDate(startOfDay, endOfDay).map { appointmentEntity ->
            appointmentEntity.toAppointmentResponse()
        }
    }

    override suspend fun addAppointment(appointmentResponse: AppointmentResponse): List<Long> {
        return appointmentDao.insertAppointmentEntity(appointmentResponse.toAppointmentEntity())
    }

    override suspend fun getAppointmentsOfPatientByDate(
        patientId: String,
        startOfDay: Long,
        endOfDay: Long
    ): AppointmentResponse? {
        return appointmentDao.getAppointmentOfPatientByDate(patientId, startOfDay, endOfDay)?.toAppointmentResponse()
    }

    override suspend fun updateAppointment(appointmentResponse: AppointmentResponse): Int {
        return appointmentDao.updateAppointmentEntity(appointmentResponse.toAppointmentEntity())
    }

    override suspend fun getAppointmentsOfPatientByStatus(
        patientId: String,
        status: String
    ): List<AppointmentResponse> {
        return appointmentDao.getAppointmentsOfPatientByStatus(patientId, status)
            .map { appointmentEntity ->
                appointmentEntity.toAppointmentResponse()
            }
    }
}