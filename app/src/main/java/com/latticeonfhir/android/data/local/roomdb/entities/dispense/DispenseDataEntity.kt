package com.latticeonfhir.android.data.local.roomdb.entities.dispense

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.latticeonfhir.core.data.local.roomdb.entities.patient.PatientEntity
import java.util.Date

@Keep
@Entity(
    indices = [Index("dispenseId")],
    primaryKeys = ["dispenseId"],
    foreignKeys = [
        ForeignKey(
            entity = PatientEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("patientId")
        )
    ]
)
data class DispenseDataEntity(
    val dispenseId: String,
    val dispenseFhirId: String?,
    val appointmentId: String?,
    val patientId: String,
    val prescriptionId: String?,
    val generatedOn: Date,
    val note: String?
)