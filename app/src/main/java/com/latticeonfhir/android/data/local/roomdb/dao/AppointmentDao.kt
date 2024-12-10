package com.latticeonfhir.android.data.local.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.latticeonfhir.android.data.local.roomdb.entities.appointment.AppointmentEntity

@Dao
interface AppointmentDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointmentEntity(vararg appointmentEntity: AppointmentEntity): List<Long>

    @Transaction
    @Query("SELECT id FROM AppointmentEntity WHERE appointmentFhirId = :appointmentFhirId")
    suspend fun getAppointmentIdByFhirId(appointmentFhirId: String): String

    @Transaction
    @Query("SELECT appointmentFhirId FROM AppointmentEntity WHERE id = :appointmentId")
    suspend fun getFhirIdByAppointmentId(appointmentId: String): String?

    @Transaction
    @Query("UPDATE AppointmentEntity SET appointmentFhirId=:fhirId WHERE id=:id")
    suspend fun updateAppointmentFhirId(id: String, fhirId: String): Int

    @Transaction
    @Query("SELECT * FROM AppointmentEntity WHERE patientId=:patientId and status=:status ORDER BY startTime")
    suspend fun getAppointmentsOfPatientByStatus(
        patientId: String,
        status: String
    ): List<AppointmentEntity>

    @Transaction
    @Query("SELECT * FROM AppointmentEntity WHERE startTime BETWEEN :startOfDay AND :endOfDay ORDER BY startTime")
    suspend fun getAppointmentsByDate(startOfDay: Long, endOfDay: Long): List<AppointmentEntity>

    @Transaction
    @Query("SELECT * FROM AppointmentEntity WHERE patientId=:patientId AND status<>\"cancelled\" AND startTime BETWEEN :startOfDay AND :endOfDay")
    suspend fun getAppointmentOfPatientByDate(
        patientId: String,
        startOfDay: Long,
        endOfDay: Long
    ): AppointmentEntity?

    @Query("SELECT * FROM AppointmentEntity WHERE patientId=:patientId ORDER BY startTime DESC")
    suspend fun getAppointmentsOfPatient(
        patientId: String
    ): List<AppointmentEntity>

    @Transaction
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAppointmentEntity(appointmentEntity: AppointmentEntity): Int

    @Transaction
    @Query("SELECT * FROM AppointmentEntity WHERE status=:status and endTime<:endOfDay")
    suspend fun getTodayScheduledAppointments(
        status: String,
        endOfDay: Long
    ): List<AppointmentEntity>

    @Transaction
    @Query("SELECT * FROM AppointmentEntity WHERE id IN (:appointmentId)")
    suspend fun getAppointmentById(vararg appointmentId: String): List<AppointmentEntity>

    @Transaction
    @Query("SELECT * FROM AppointmentEntity WHERE patientId=:patientId AND (status=\"completed\" OR status=\"in-progress\") ORDER BY startTime DESC LIMIT 1")
    suspend fun getLastCompletedAppointment(
        patientId: String
    ): AppointmentEntity?
}