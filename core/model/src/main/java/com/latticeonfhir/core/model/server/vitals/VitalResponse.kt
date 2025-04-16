package com.latticeonfhir.core.model.server.vitals

import androidx.annotation.Keep

@Keep
data class VitalResponse(
    val vitalUuid: String,
    val appointmentId: String,
    val bloodGlucose: String?,
    val bloodGlucoseType: String?,
    val bloodGlucoseUnit: String?,
    val bpDiastolic: String?,
    val bpSystolic: String?,
    val createdOn: String,
    val eyeTestType: String?,
    val heartRate: String?,
    val heightCm: String?,
    val heightFt: String?,
    val heightInch: String?,
    val leftEye: String?,
    val patientId: String?,
    val respRate: String?,
    val rightEye: String?,
    val spo2: String?,
    val temp: String?,
    val tempUnit: String?,
    val weight: String?,
    val practitionerName: String?,
    val vitalFhirId: String?,
    val cholesterol: Double?,
    val cholesterolUnit: String?
)
