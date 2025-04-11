package com.latticeonfhir.core.model.server.labormed.labtest

import androidx.annotation.Keep
import com.latticeonfhir.core.model.server.labormed.Document

@Keep
data class DiagnosticReport(
    val createdOn: String,
    val diagnosticReportFhirId: String,
    val diagnosticUuid: String,
    val documents: List<Document>,
    val resourceType: String,
    val status: String
)