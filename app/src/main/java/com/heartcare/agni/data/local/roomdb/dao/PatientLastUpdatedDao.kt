package com.heartcare.agni.data.local.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import com.heartcare.agni.data.local.roomdb.entities.patient.PatientLastUpdatedEntity

@Dao
interface PatientLastUpdatedDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatientLastUpdatedData(vararg patientLastUpdatedEntity: PatientLastUpdatedEntity): List<Long>
}