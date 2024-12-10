package com.latticeonfhir.android.data.server.model.labormed.medicalrecord

import androidx.annotation.Keep
import com.latticeonfhir.android.data.server.model.labormed.MedDocument

@Keep
data class MedicalRecord(
    val createdOn: String,
    val documents: List<MedDocument>,
    val medicalRecordFhirId: String,
    val medicalReportUuid: String,
    val resourceType: String,
    val status: String
)