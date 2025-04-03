package com.latticeonfhir.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.latticeonfhir.core.database.entities.vitals.VitalEntity

@Dao
interface VitalDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVital(vararg vitalEntity: VitalEntity): List<Long>

    @Transaction
    @Query("SELECT * FROM VitalEntity vital WHERE patientId = :patientId ORDER BY vital.createdOn DESC LIMIT :limit")
    suspend fun getPastVitals(
        patientId: String,
        limit: Int = 5
    ): List<VitalEntity>

    @Transaction
    @Query("SELECT * FROM VitalEntity WHERE patientId=:patientId")
    suspend fun getPastVitals(
        patientId: String
    ): List<VitalEntity>

    @Transaction
    @Query("UPDATE VitalEntity SET fhirId = :fhirId WHERE vitalUuid = :vitalUUid")
    suspend fun updateVitalFhirId(vitalUUid: String, fhirId: String)

    @Transaction
    @Query("SELECT * FROM VitalEntity WHERE appointmentId = :appointmentId")
    suspend fun getVitalsByAppointmentId(appointmentId: String): List<VitalEntity>

    @Transaction
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateVitalData(vitalEntity: VitalEntity): Int
}