package com.latticeonfhir.android.data.server.model.cvd

import androidx.annotation.Keep
import java.util.Date

@Keep
data class CVDResponse(
    val cvdFhirId: String?,
    val cvdUuid: String,
    val appointmentId: String,
    val patientId: String,
    val bmi: Int,
    val bpDiastolic: Int,
    val bpSystolic: Int,
    val cholesterol: Int,
    val cholesterolUnit: String,
    val diabetic: Int,
    val heightCm: Int?,
    val heightFt: Int?,
    val heightInch: Int?,
    val practitionerName: String?,
    val risk: Int,
    val smoker: Int,
    val weight: Int,
    val createdOn: Date
)