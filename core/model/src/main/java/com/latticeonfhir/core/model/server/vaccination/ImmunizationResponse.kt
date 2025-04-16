package com.latticeonfhir.core.model.server.vaccination

import androidx.annotation.Keep
import java.util.Date

@Keep
data class ImmunizationResponse(
    val appointmentId: String,
    val createdOn: Date,
    val expiryDate: Date,
    val immunizationFiles: List<ImmunizationFile>?,
    val immunizationId: String?,
    val immunizationUuid: String,
    val lotNumber: String,
    val manufacturerId: String?,
    val notes: String?,
    val patientId: String,
    val vaccineCode: String
)