package com.latticeonfhir.android.data.server.model

import com.latticeonfhir.android.base.parcelclass.ParcelableClass
import com.latticeonfhir.android.data.local.roomdb.entities.PermanentAddressEntity
import java.util.Date
import java.util.Objects

data class PersonResponse(
    val firstName: String,
    val middleName: String,
    val lastName: String,
    val identifier: List<PersonIdentifier>?,
    val active: Boolean,
    val gender: String,
    val birthDate: Date,
    val mobileNumber: Long,
    val email: String,
    val permanentAddress: PermanentAddressEntity
): ParcelableClass()
