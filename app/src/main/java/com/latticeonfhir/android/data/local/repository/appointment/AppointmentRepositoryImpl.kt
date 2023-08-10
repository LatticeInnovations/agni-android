package com.latticeonfhir.android.data.local.repository.appointment

import com.latticeonfhir.android.data.local.model.appointment.AppointmentResponseLocal
import com.latticeonfhir.android.data.local.roomdb.dao.AppointmentDao
import com.latticeonfhir.android.utils.converters.responseconverter.toAppointmentEntity
import com.latticeonfhir.android.utils.converters.responseconverter.toAppointmentResponseLocal
import javax.inject.Inject

class AppointmentRepositoryImpl @Inject constructor(private val appointmentDao: AppointmentDao) :
    AppointmentRepository {

    override suspend fun getAppointmentListByDate(
        startOfDay: Long,
        endOfDay: Long
    ): List<AppointmentResponseLocal> {
        return appointmentDao.getAppointmentsByDate(startOfDay, endOfDay).map { appointmentEntity ->
            appointmentEntity.toAppointmentResponseLocal()
        }
    }

    override suspend fun addAppointment(appointmentResponseLocal: AppointmentResponseLocal): List<Long> {
        return appointmentDao.insertAppointmentEntity(appointmentResponseLocal.toAppointmentEntity())
    }

    override suspend fun getAppointmentsOfPatientByDate(
        patientId: String,
        startOfDay: Long,
        endOfDay: Long
    ): AppointmentResponseLocal? {
        return appointmentDao.getAppointmentOfPatientByDate(patientId, startOfDay, endOfDay)
            ?.toAppointmentResponseLocal()
    }

    override suspend fun updateAppointment(appointmentResponseLocal: AppointmentResponseLocal): Int {
        return appointmentDao.updateAppointmentEntity(appointmentResponseLocal.toAppointmentEntity())
    }

    override suspend fun getAppointmentsOfPatientByStatus(
        patientId: String,
        status: String
    ): List<AppointmentResponseLocal> {
        return appointmentDao.getAppointmentsOfPatientByStatus(patientId, status)
            .map { appointmentEntity ->
                appointmentEntity.toAppointmentResponseLocal()
            }
    }
}