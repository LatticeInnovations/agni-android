package com.latticeonfhir.android.data.local.model.vital

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import java.util.Date

@Keep
@Parcelize
data class VitalLocal(
    val vitalUuid: String,
    val fhirId: String?,
    val patientId: String?,
    val appointmentId: String,
    val bloodGlucose: String?,
    val bloodGlucoseType: String?,
    val bloodGlucoseUnit: String?,
    val bpDiastolic: String?,
    val bpSystolic: String?,
    val createdOn: Date,
    val eyeTestType: String?,
    val heartRate: String?,
    val heightCm: String?,
    val heightFt: String?,
    val heightInch: String?,
    val leftEye: Int?,
    val respRate: String?,
    val rightEye: Int?,
    val spo2: String?,
    val temp: String?,
    val tempUnit: String?,
    val weight: String?,
    val practitionerName: String?
) : Parcelable
