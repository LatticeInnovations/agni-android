package com.latticeonfhir.android.data.local.roomdb.entities.medication

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

@Keep
@Entity
data class MedicationEntity(
    @PrimaryKey
    val medFhirId: String,
    val medCodeName: String,
    val medName: String,
    val doseForm: String,
    val doseFormCode: String,
    val activeIngredient: String,
    val activeIngredientCode: String,
    val medUnit: String,
    val medNumeratorVal: Double
)