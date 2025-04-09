package com.latticeonfhir.core.data.server.model.patient

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class PatientResponse(
    val id: String,
    val firstName: String,
    val middleName: String?,
    val lastName: String?,
    val identifier: List<PatientIdentifier>,
    val active: Boolean?,
    val gender: String,
    val birthDate: String,
    val mobileNumber: Long,
    val email: String?,
    val permanentAddress: PatientAddressResponse,
    val fhirId: String?
) : Parcelable
