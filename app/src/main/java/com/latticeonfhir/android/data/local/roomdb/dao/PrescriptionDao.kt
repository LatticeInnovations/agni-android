package com.latticeonfhir.android.data.local.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.latticeonfhir.android.data.local.roomdb.entities.medication.MedicationEntity
import com.latticeonfhir.android.data.local.roomdb.entities.medication.MedicineDosageInstructionsEntity
import com.latticeonfhir.android.data.local.roomdb.entities.prescription.PrescriptionAndMedicineRelation
import com.latticeonfhir.android.data.local.roomdb.entities.prescription.PrescriptionEntity
import com.latticeonfhir.android.data.local.roomdb.entities.prescription.PrescriptionDirectionsEntity

@Dao
interface PrescriptionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrescription(vararg prescriptionEntity: PrescriptionEntity): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrescriptionMedicines(vararg prescriptionDirectionsEntity: PrescriptionDirectionsEntity): List<Long>

//    @Query("SELECT * FROM PrescriptionEntity prescription INNER JOIN (SELECT * FROM PrescriptionDirectionsEntity prescriptionDirections INNER JOIN MedicationEntity medication ON prescriptionDirections.medFhirId = medication.medFhirId) prescribeMedicines ON prescription.id = prescribeMedicines.prescriptionId AND prescription.patientId = :patientId ORDER BY prescription.prescriptionDate LIMIT :limit")
//    suspend fun getPastPrescriptions(patientId: String, limit: Int = 5): List<PrescriptionAndMedicineRelation>

    @Query("SELECT * FROM PrescriptionEntity WHERE patientId = :patientId ORDER BY prescriptionDate DESC LIMIT :limit")
    suspend fun getPastPrescriptions(patientId: String, limit: Int = 5): List<PrescriptionAndMedicineRelation>
}