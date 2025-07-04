package com.heartcare.agni.data.local.roomdb.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.heartcare.agni.data.local.roomdb.entities.labtestandmedrecord.LabTestAndMedEntity
import com.heartcare.agni.data.local.roomdb.entities.labtestandmedrecord.photo.LabTestAndFileEntity
import com.heartcare.agni.data.local.roomdb.entities.labtestandmedrecord.photo.LabTestAndMedPhotoEntity

@Dao
interface LabTestAndMedDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLabAndMedTest(vararg labTestAndMedEntity: LabTestAndMedEntity): List<Long>

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLabTestsAndMedPhotos(vararg labTestAndMedPhotoEntity: LabTestAndMedPhotoEntity): List<Long>

    @Transaction
    @Query("SELECT * FROM LabTestAndMedEntity WHERE appointmentId = :appointmentId")
    suspend fun getLabTestAndMed(
        appointmentId: String
    ): LabTestAndMedEntity

    @Transaction
    @Query("SELECT * FROM LabTestAndMedEntity WHERE patientId=:patientId AND type=:photoviewType")
    suspend fun getPastPhotoLabAndMedTests(
        patientId: String, photoviewType: String
    ): List<LabTestAndFileEntity>

    @Transaction
    @Query("UPDATE LabTestAndMedEntity SET labTestFhirId = :labTestFhirId WHERE id = :labTestId")
    suspend fun updateLabTestAndFhirId(labTestId: String, labTestFhirId: String)

    @Transaction
    @Query("UPDATE LabTestAndMedPhotoEntity SET fhirId = :labTestFhirId WHERE id = :labTestId")
    suspend fun updateLabTestAndMedPhotoFhirId(labTestId: String, labTestFhirId: String)

    @Transaction
    @Query("SELECT * FROM LabTestAndMedEntity WHERE patientId = :patientId AND type=:photoviewType")
    suspend fun getLabTestAndMedPhotoByPatientId(
        patientId: String,
        photoviewType: String
    ): List<LabTestAndFileEntity>

    @Transaction
    @Query("SELECT * FROM LabTestAndMedEntity WHERE createdOn BETWEEN :startDate AND :endDate AND patientId=:patientId AND type= :photoviewType")
    suspend fun getLabTestAndPhotoByDate(
        patientId: String, photoviewType: String, startDate: Long, endDate: Long
    ): List<LabTestAndFileEntity>

    @Delete
    suspend fun deleteLabTestAndMedPhoto(labTestAndMedPhotoEntity: LabTestAndMedPhotoEntity): Int

    @Delete
    suspend fun deleteLabTestAndMedEntity(labTestAndMedEntity: LabTestAndMedEntity): Int

    @Transaction
    @Query("UPDATE LabTestAndMedPhotoEntity SET fhirId = :documentFhirId WHERE id = :documentUuid")
    suspend fun updateDocumentFhirId(documentUuid: String, documentFhirId: String)

    @Query("delete from labtestandmedphotoentity where fileName=:fileName")
    suspend fun deleteLabTestAndMedPhoto(fileName: String): Int

    @Query("delete from labtestandmedentity where id=:labTestId")
    suspend fun deleteLabTestAndMedEntity(labTestId: String): Int

}