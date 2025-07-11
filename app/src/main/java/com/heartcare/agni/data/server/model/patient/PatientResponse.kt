package com.heartcare.agni.data.server.model.patient

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import java.util.Date

@Keep
@Parcelize
data class PatientResponse(
    val birthDate: String,
    val fathersName: String?,
    val fhirId: String?,
    val firstName: String,
    val gender: String,
    val generalPractitioner: List<GeneralPractitioner>?,
    val id: String,
    val identifier: List<PatientIdentifier>,
    val isDeleted: Boolean?,
    val lastName: String,
    val managingOrganization: ManagingOrganization?,
    val mobileNumber: String?,
    val mothersName: String,
    val patientDeceasedReason: String?,
    val patientDeceasedReasonId: Int?,
    val permanentAddress: PatientAddressResponse,
    val spouseName: String?,
    val appUpdatedDate: Date?,
    val active: Boolean?
) : Parcelable
