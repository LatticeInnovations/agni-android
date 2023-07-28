package com.latticeonfhir.android.data.local.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.latticeonfhir.android.data.local.roomdb.entities.appointment.AppointmentEntity
import java.util.Date

@Dao
interface AppointmentDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointmentEntity(vararg appointmentEntity: AppointmentEntity): List<Long>

    @Transaction
    @Query("UPDATE AppointmentEntity SET appointmentFhirId=:fhirId WHERE id=:id")
    suspend fun updateAppointmentFhirId(id: String, fhirId: String): Int

    @Transaction
    @Query("SELECT * FROM AppointmentEntity WHERE patientId=:patientId and status=:status")
    suspend fun getAppointmentsOfPatientByStatus(patientId: String, status: String): List<AppointmentEntity>

    @Transaction
    @Query("SELECT * FROM AppointmentEntity WHERE startTime BETWEEN :startOfDay AND :endOfDay")
    suspend fun getAppointmentsByDate(startOfDay: Date, endOfDay: Date): List<AppointmentEntity>

    @Transaction
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAppointmentEntity(appointmentEntity: AppointmentEntity): Int

    @Transaction
    @Query("SELECT * FROM AppointmentEntity WHERE status=:status and endTime<:endOfDay")
    suspend fun getTodayScheduledAppointments(status: String, endOfDay: Date): List<AppointmentEntity>
}