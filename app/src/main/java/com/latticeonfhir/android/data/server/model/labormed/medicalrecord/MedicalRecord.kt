package com.latticeonfhir.android.data.server.model.labormed.medicalrecord

import androidx.annotation.Keep
import com.latticeonfhir.android.data.server.model.labormed.Document

@Keep
data class MedicalRecord(
    val createdOn: String,
    val documents: List<Document>,
    val medicalRecordFhirId: String,
    val medicalReportUuid: String,
    val resourceType: String
)