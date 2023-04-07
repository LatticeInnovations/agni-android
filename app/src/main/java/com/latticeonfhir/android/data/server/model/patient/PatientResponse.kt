package com.latticeonfhir.android.data.server.model.patient

import com.latticeonfhir.android.base.baseclass.ParcelableClass
import java.time.LocalDate
import java.util.Date

data class PatientResponse(
    val id: String,
    val firstName: String,
    val middleName: String?,
    val lastName: String?,
    val identifier: List<PatientIdentifier>,
    val active: Boolean?,
    val gender: String,
    val birthDate: LocalDate,
    val mobileNumber: Long?,
    val email: String?,
    val permanentAddress: PatientAddressResponse,
    val fhirId: String?
): ParcelableClass()
