package com.latticeonfhir.android.data.local.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.latticeonfhir.android.data.local.roomdb.entities.prescription.PrescriptionAndMedicineRelation
import com.latticeonfhir.android.data.local.roomdb.entities.prescription.PrescriptionDirectionsEntity
import com.latticeonfhir.android.data.local.roomdb.entities.prescription.PrescriptionEntity
import com.latticeonfhir.android.data.local.roomdb.entities.prescription.photo.PrescriptionAndFileEntity
import com.latticeonfhir.android.data.local.roomdb.entities.prescription.photo.PrescriptionPhotoEntity

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
    @Query("SELECT * FROM PrescriptionEntity prescription WHERE patientId = :patientId ORDER BY prescription.prescriptionDate DESC LIMIT :limit")
    suspend fun getPastPrescriptions(
        patientId: String,
        limit: Int = 5
    ): List<PrescriptionAndMedicineRelation>

    @Transaction
    @Query("SELECT fileName FROM PrescriptionPhotoEntity INNER JOIN PrescriptionEntity ON PrescriptionPhotoEntity.prescriptionId = PrescriptionEntity.id WHERE PrescriptionEntity.patientId=:patientId")
    suspend fun getPastPhotoPrescriptions(
        patientId: String
    ): List<String>

    @Transaction
    @Query("UPDATE PrescriptionEntity SET prescriptionFhirId = :prescriptionFhirId WHERE id = :prescriptionId")
    suspend fun updatePrescriptionFhirId(prescriptionId: String, prescriptionFhirId: String)

    @Transaction
    @Query("SELECT * FROM PrescriptionEntity WHERE appointmentId = :appointmentId")
    suspend fun getPrescriptionByAppointmentId(appointmentId: String): List<PrescriptionAndMedicineRelation>

    @Transaction
    @Query("SELECT * FROM PrescriptionEntity WHERE appointmentId = :appointmentId")
    suspend fun getPrescriptionPhotoByAppointmentId(appointmentId: String): List<PrescriptionAndFileEntity>
}