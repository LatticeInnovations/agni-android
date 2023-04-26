package com.latticeonfhir.android.data.server.model.patient

import androidx.annotation.Keep
import com.latticeonfhir.android.base.baseclass.ParcelableClass

@Keep
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
): ParcelableClass()
