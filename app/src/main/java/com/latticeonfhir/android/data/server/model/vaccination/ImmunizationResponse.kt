package com.latticeonfhir.android.data.server.model.vaccination

import androidx.annotation.Keep

@Keep
data class ImmunizationResponse(
    val appointmentId: String,
    val createdOn: String,
    val expiryDate: String,
    val immunizationFiles: List<ImmunizationFile>,
    val immunizationId: String?,
    val immunizationUuid: String,
    val lotNumber: String,
    val manufacturerId: String,
    val notes: String,
    val patientId: String,
    val vaccineCode: String
)