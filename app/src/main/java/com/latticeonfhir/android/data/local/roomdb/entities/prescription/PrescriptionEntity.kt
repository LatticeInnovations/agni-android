package com.latticeonfhir.android.data.local.roomdb.entities.prescription

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.errorprone.annotations.Keep
import com.latticeonfhir.android.data.local.roomdb.entities.patient.PatientEntity

@Keep
@Entity(
    foreignKeys = [ForeignKey(
        entity = PatientEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("patientId")
    )]
)
data class PrescriptionEntity(
    @PrimaryKey val id: String,
    val prescriptionDate: Long,
    val patientId: String,
    val prescriptionFhirId: String?
)
