package com.heartcare.agni.data.local.roomdb.entities.dispense

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.heartcare.agni.data.local.roomdb.entities.medication.MedicationEntity
import com.heartcare.agni.data.local.roomdb.entities.patient.PatientEntity
import java.util.Date

@Keep
@Entity(
    indices = [Index("medDispenseUuid"), Index("patientId"), Index("dispenseId"), Index("prescribedMedFhirId"), Index("dispensedMedFhirId")],
    primaryKeys = ["medDispenseUuid"],
    foreignKeys = [
        ForeignKey(
            entity = PatientEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("patientId")
        ),
        ForeignKey(
            entity = DispenseDataEntity::class,
            parentColumns = arrayOf("dispenseId"),
            childColumns = arrayOf("dispenseId")
        ),
        ForeignKey(
            entity = MedicationEntity::class,
            parentColumns = arrayOf("medFhirId"),
            childColumns = arrayOf("prescribedMedFhirId")
        ),
        ForeignKey(
            entity = MedicationEntity::class,
            parentColumns = arrayOf("medFhirId"),
            childColumns = arrayOf("dispensedMedFhirId")
        )
    ]
)
data class MedicineDispenseListEntity(
    val medDispenseFhirId: String?,
    val medDispenseUuid: String,
    val dispenseId: String,
    val patientId: String,
    val qtyDispensed: Int,
    val qtyPrescribed: Int,
    val prescribedMedFhirId: String,
    val prescribedMedReqId: String?,
    val date: Date,
    val category: String,
    val isModified: Boolean,
    val modificationType: String?,
    val medNote: String?,
    val dispensedMedFhirId: String
)
