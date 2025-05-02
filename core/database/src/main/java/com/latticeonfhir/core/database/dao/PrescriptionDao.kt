package com.latticeonfhir.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.latticeonfhir.core.database.entities.prescription.PrescriptionAndMedicineRelation
import com.latticeonfhir.core.database.entities.prescription.PrescriptionDirectionsEntity
import com.latticeonfhir.core.database.entities.prescription.PrescriptionEntity
import com.latticeonfhir.core.database.entities.prescription.photo.PrescriptionAndFileEntity
import com.latticeonfhir.core.database.entities.prescription.photo.PrescriptionPhotoEntity
import com.latticeonfhir.core.model.enums.PrescriptionType

@Dao
interface PrescriptionDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrescription(vararg prescriptionEntity: PrescriptionEntity): List<Long>

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrescriptionMedicines(vararg prescriptionDirectionsEntity: PrescriptionDirectionsEntity): List<Long>

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrescriptionPhotos(vararg prescriptionPhotoEntity: PrescriptionPhotoEntity): List<Long>

    @Transaction
    @Query("SELECT * FROM PrescriptionEntity prescription WHERE patientId = :patientId AND prescriptionType=:prescriptionType ORDER BY prescription.prescriptionDate DESC")
    suspend fun getPastPrescriptions(
        patientId: String,
        prescriptionType: String = PrescriptionType.FORM.type
    ): List<PrescriptionAndMedicineRelation>

    @Transaction
    @Query("SELECT * FROM PrescriptionEntity WHERE patientId=:patientId AND prescriptionType=:prescriptionType ")
    suspend fun getPastPhotoPrescriptions(
        patientId: String,
        prescriptionType: String = PrescriptionType.PHOTO.type
    ): List<PrescriptionAndFileEntity>

    @Transaction
    @Query("UPDATE PrescriptionEntity SET prescriptionFhirId = :prescriptionFhirId WHERE id = :prescriptionId")
    suspend fun updatePrescriptionFhirId(prescriptionId: String, prescriptionFhirId: String)

    @Transaction
    @Query("SELECT * FROM PrescriptionEntity WHERE appointmentId = :appointmentId")
    suspend fun getPrescriptionByAppointmentId(appointmentId: String): List<PrescriptionAndMedicineRelation>

    @Transaction
    @Query("SELECT * FROM PrescriptionEntity WHERE appointmentId = :appointmentId")
    suspend fun getPrescriptionPhotoByAppointmentId(appointmentId: String): List<PrescriptionAndFileEntity>

    @Transaction
    @Query("SELECT * FROM PrescriptionEntity WHERE prescriptionDate BETWEEN :startDate AND :endDate AND patientId=:patientId")
    suspend fun getPrescriptionPhotoByDate(
        patientId: String,
        startDate: Long,
        endDate: Long
    ): List<PrescriptionAndFileEntity>

    @Transaction
    @Query("SELECT * FROM PrescriptionEntity WHERE id=:prescriptionId")
    suspend fun getPrescriptionPhotoById(prescriptionId: String): PrescriptionAndFileEntity

    @Delete
    suspend fun deletePrescriptionPhoto(prescriptionPhotoEntity: PrescriptionPhotoEntity): Int

    @Delete
    suspend fun deletePrescriptionEntity(prescriptionEntity: PrescriptionEntity): Int

    @Transaction
    @Query("UPDATE PrescriptionDirectionsEntity SET medReqFhirId = :medReqFhirId WHERE id = :medReqUuid")
    suspend fun updateMedReqFhirId(medReqUuid: String, medReqFhirId: String)

    @Transaction
    @Query("UPDATE PrescriptionPhotoEntity SET documentFhirId = :documentFhirId WHERE id = :documentUuid")
    suspend fun updateDocumentFhirId(documentUuid: String, documentFhirId: String)

    @Transaction
    @Query("SELECT * FROM PrescriptionEntity WHERE id IN (:prescriptionId)")
    suspend fun getPrescriptionById(vararg prescriptionId: String): List<PrescriptionEntity>

    @Transaction
    @Query("SELECT * FROM PrescriptionDirectionsEntity WHERE id IN (:medReqUuid)")
    suspend fun getMedReqById(vararg medReqUuid: String): List<PrescriptionDirectionsEntity>

    @Transaction
    @Query("SELECT id FROM PrescriptionEntity WHERE prescriptionFhirId=:fhirId")
    suspend fun getPrescriptionIdByFhirId(fhirId: String): String

    @Transaction
    @Query("SELECT * FROM PrescriptionEntity where prescriptionType=\"form\"")
    suspend fun getAllFormPrescriptions() : List<PrescriptionEntity>
}