package com.heartcare.agni.data.local.roomdb.entities.symptomsanddiagnosis

import android.os.Parcelable
import androidx.annotation.Keep
import com.heartcare.agni.data.server.model.symptomsanddiagnosis.SymptomsAndDiagnosisItem
import kotlinx.parcelize.Parcelize
import java.util.Date

@Keep
@Parcelize
data class SymptomsAndDiagnosisLocal(
    val symDiagUuid: String,
    val appointmentId: String,
    val symDiagFhirId: String?,
    val createdOn: Date,
    val diagnosis: List<SymptomsAndDiagnosisItem>,
    val symptoms: List<SymptomsAndDiagnosisItem>,
    val practitionerName: String?,
    val patientId: String?,
) : Parcelable
