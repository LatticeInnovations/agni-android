package com.latticeonfhir.android.data.local.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.latticeonfhir.android.data.local.roomdb.entities.symptomsanddiagnosis.DiagnosisEntity
import com.latticeonfhir.android.data.local.roomdb.entities.symptomsanddiagnosis.SymptomAndDiagnosisEntity
import com.latticeonfhir.android.data.local.roomdb.entities.symptomsanddiagnosis.SymptomsEntity

@Dao
interface SymptomsAndDiagnosisDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSymptomsEntity(vararg symptomsEntity: SymptomsEntity): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiagnosisEntity(vararg diagnosisEntity: DiagnosisEntity): List<Long>

    @Transaction
    @Query("SELECT * FROM symptoms")
    suspend fun getSymptomsEntity(): List<SymptomsEntity>

    @Transaction
    @Query("SELECT * FROM diagnosis")
    suspend fun getDiagnosisEntity(): List<DiagnosisEntity>

    // insert, get, update symptoms and Diagnosis

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSymptomsAndDiagnosis(vararg symptomAndDiagnosisEntity: SymptomAndDiagnosisEntity): List<Long>

    @Transaction
    @Query("SELECT * FROM SymptomAndDiagnosisEntity WHERE patientId=:patientId")
    suspend fun getPastSymptomsAndDiagnosis(
        patientId: String
    ): List<SymptomAndDiagnosisEntity>

    @Transaction
    @Query("UPDATE SymptomAndDiagnosisEntity SET fhirId = :fhirId WHERE symDiagUuid = :symDiagUuid")
    suspend fun updateSymDiagFhirId(symDiagUuid: String, fhirId: String)

    @Transaction
    @Query("SELECT * FROM SymptomAndDiagnosisEntity WHERE appointmentId = :appointmentId")
    suspend fun getSymDiagByAppointmentId(appointmentId: String): List<SymptomAndDiagnosisEntity>

    @Transaction
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSymDiagData(symptomAndDiagnosisEntity: SymptomAndDiagnosisEntity): Int
}