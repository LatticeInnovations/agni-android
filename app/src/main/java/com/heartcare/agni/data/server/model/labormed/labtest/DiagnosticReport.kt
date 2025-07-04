package com.heartcare.agni.data.server.model.labormed.labtest

import androidx.annotation.Keep
import com.heartcare.agni.data.server.model.labormed.Document

@Keep
data class DiagnosticReport(
    val createdOn: String,
    val diagnosticReportFhirId: String,
    val diagnosticUuid: String,
    val documents: List<Document>,
    val resourceType: String,
    val status: String
)