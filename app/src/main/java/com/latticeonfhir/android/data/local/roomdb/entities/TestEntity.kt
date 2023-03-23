package com.latticeonfhir.android.data.local.roomdb.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

@Entity
@Fts4
data class TestEntity(
    val firstName: String,
    val middleName: String?,
    val lastName: String?,
    val active: Boolean?,
    val gender: String,
    val birthDate: Long,
    val mobileNumber: Long?,
    val email: String?,
    @Embedded val permanentAddress: PermanentAddressEntity,
    val fhirId: String?
) {
    @PrimaryKey(autoGenerate = true)
    var rowid: Int = 1
}