package com.heartcare.agni.data.local.roomdb.entities.patient

import androidx.annotation.Keep
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Keep
@Entity(indices = [Index("fhirId")])
data class PatientEntity(
    @PrimaryKey
    val id: String,
    val firstName: String,
    val lastName: String,
    val gender: String,
    val birthDate: Long,
    val mobileNumber: Long?,
    @Embedded val permanentAddress: PermanentAddressEntity,
    val fhirId: String?,
    val fathersName: String?,
    val generalPractitioner: String?,
    val isDeleted: Boolean?,
    val managingOrganization: String?,
    val mothersName: String,
    val patientDeceasedReason: String?,
    val patientDeceasedReasonId: Int?,
    val spouseName: String?,
    val active: Boolean?
)
