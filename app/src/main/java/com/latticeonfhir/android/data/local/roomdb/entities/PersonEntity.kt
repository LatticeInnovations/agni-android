package com.latticeonfhir.android.data.local.roomdb.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PersonEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val gender: String,
    val mobileNumber: Long
)
