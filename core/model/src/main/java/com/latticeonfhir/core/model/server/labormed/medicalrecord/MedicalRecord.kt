package com.latticeonfhir.core.model.server.labormed.medicalrecord

import androidx.annotation.Keep
import com.latticeonfhir.core.model.server.labormed.MedDocument

@Keep
data class MedicalRecord(
    val createdOn: String,
    val documents: List<MedDocument>,
    val medicalRecordFhirId: String,
    val medicalReportUuid: String,
    val resourceType: String,
    val status: String
)