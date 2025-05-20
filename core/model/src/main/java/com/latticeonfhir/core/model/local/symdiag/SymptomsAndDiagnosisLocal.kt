package com.latticeonfhir.core.model.local.symdiag

import android.os.Parcelable
import androidx.annotation.Keep
import com.latticeonfhir.core.model.server.symptomsanddiagnosis.SymptomsAndDiagnosisItem
import kotlinx.parcelize.Parcelize
import java.util.Date

@Keep
data class SymptomsAndDiagnosisData(
    val patientId: String?,
    val appointmentId: String,
    val symDiagUuid: String,
    val createdOn: Date,
    val diagnosis: List<String>,
    val symptoms: List<String>,
)

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