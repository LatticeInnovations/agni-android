package com.latticeonfhir.android.data.server.model.cvd

import androidx.annotation.Keep
import java.util.Date

@Keep
data class CVDResponse(
    val cvdFhirId: String?,
    val cvdUuid: String,
    val appointmentId: String,
    val patientId: String,
    val bmi: Double?,
    val bpDiastolic: Int,
    val bpSystolic: Int,
    val cholesterol: Double?,
    val cholesterolUnit: String?,
    val diabetic: Int,
    val heightCm: Double?,
    val heightFt: Int?,
    val heightInch: Double?,
    val practitionerName: String?,
    val risk: Int,
    val smoker: Int,
    val weight: Double?,
    val createdOn: Date
)