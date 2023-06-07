package com.latticeonfhir.android.data.local.roomdb.entities.patient

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
    val middleName: String?,
    val lastName: String?,
    val active: Boolean?,
    val gender: String,
    val birthDate: Long,
    val mobileNumber: Long,
    val email: String?,
    @Embedded val permanentAddress: PermanentAddressEntity,
    val fhirId: String?
)
