package com.latticeonfhir.android.data.local.roomdb.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.Objects

@Entity
data class PersonEntity(
    @PrimaryKey
    val id: String,
    val firstName: String,
    val middleName: String,
    val lastName: String,
    val identifier: String,
    val active: Boolean,
    val gender: String,
    val birthDate: Long,
    val mobileNumber: Long,
    val email: String,
    @Embedded val permanentAddress: PermanentAddressEntity
)
