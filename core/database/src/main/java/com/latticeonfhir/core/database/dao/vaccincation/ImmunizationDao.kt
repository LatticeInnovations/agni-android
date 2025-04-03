package com.latticeonfhir.core.database.dao.vaccincation

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.latticeonfhir.core.database.entities.vaccination.ImmunizationEntity
import com.latticeonfhir.core.database.entities.vaccination.ImmunizationFileEntity
import java.util.Date

@Dao
interface ImmunizationDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImmunization(vararg immunizationEntity: ImmunizationEntity): List<Long>

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImmunizationFiles(vararg immunizationFileEntity: ImmunizationFileEntity): List<Long>

    @Query("SELECT * FROM ImmunizationEntity WHERE patientId = :patientId")
    suspend fun getImmunizationByPatientId(patientId: String): List<ImmunizationEntity>

    @Query("SELECT * FROM ImmunizationEntity WHERE createdOn = :createdOn")
    suspend fun getImmunizationByTime(createdOn: Long): ImmunizationEntity

    @Query("SELECT * FROM ImmunizationEntity WHERE appointmentId = :appointmentId")
    suspend fun getImmunizationByAppointmentId(appointmentId: String): List<ImmunizationEntity>

    @Query("SELECT * FROM ImmunizationFileEntity WHERE immunizationId = :id")
    suspend fun getFileNameByImmunizationId(id: String): List<ImmunizationFileEntity>

    @Query("SELECT createdOn FROM ImmunizationEntity WHERE patientId = :patientId AND vaccineCode = :vaccineCode ORDER BY createdOn")
    suspend fun getVaccineTakenDate(patientId: String, vaccineCode: String): List<Date>

    @Query("UPDATE ImmunizationEntity SET immunizationFhirId = :fhirId WHERE id = :id")
    suspend fun updateFhirId(id: String, fhirId: String): Int
}