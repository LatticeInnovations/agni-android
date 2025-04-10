package com.latticeonfhir.core.database.entities.symptomsanddiagnosis

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.latticeonfhir.core.data.server.model.symptomsanddiagnosis.SymptomsAndDiagnosisItem
import com.latticeonfhir.core.database.entities.patient.PatientEntity
import java.util.Date

@Entity(
    indices = [Index("fhirId"), Index("appointmentId"), Index("patientId")],
    foreignKeys = [ForeignKey(
        entity = PatientEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("patientId")
    )]
)
@Keep
data class SymptomAndDiagnosisEntity(
    @PrimaryKey
    val symDiagUuid: String,
    val appointmentId: String,
    val fhirId: String?,
    val createdOn: Date,
    val diagnosis: List<SymptomsAndDiagnosisItem>,
    val symptoms: List<SymptomsAndDiagnosisItem>,
    val practitionerName: String,
    val patientId: String,
)