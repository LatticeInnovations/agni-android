package com.latticeonfhir.core.data.repository.local.appointment

import com.latticeonfhir.core.database.dao.AppointmentDao
import com.latticeonfhir.core.database.entities.appointment.AppointmentEntity
import com.latticeonfhir.core.model.local.appointment.AppointmentResponseLocal
import com.latticeonfhir.core.network.utils.responseconverter.toAppointmentEntity
import com.latticeonfhir.core.network.utils.responseconverter.toAppointmentResponseLocal
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

    override suspend fun getAppointmentsOfPatient(
        patientId: String
    ): List<AppointmentResponseLocal> {
        return appointmentDao.getAppointmentsOfPatient(patientId).map {
            it.toAppointmentResponseLocal()
        }
    }

    override suspend fun getAppointmentsOfPatientByDate(
        patientId: String,
        startOfDay: Long,
        endOfDay: Long
    ): AppointmentResponseLocal? {
        return appointmentDao.getAppointmentOfPatientByDate(patientId, startOfDay, endOfDay)
            ?.toAppointmentResponseLocal()
    }

    override suspend fun getAppointmentByAppointmentId(appointmentId: String): AppointmentResponseLocal {
        return appointmentDao.getAppointmentById(appointmentId)[0].toAppointmentResponseLocal()
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

    override suspend fun getLastCompletedAppointment(
        patientId: String
    ): AppointmentEntity? {
        return appointmentDao.getLastCompletedAppointment(patientId)
    }
}