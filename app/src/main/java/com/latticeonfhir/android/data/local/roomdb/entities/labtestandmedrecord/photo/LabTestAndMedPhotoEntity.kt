package com.latticeonfhir.core.data.local.roomdb.entities.labtestandmedrecord.photo

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.ForeignKey
import com.latticeonfhir.android.data.local.roomdb.entities.labtestandmedrecord.LabTestAndMedEntity

@Keep
@Entity(
    primaryKeys = ["labTestId", "fileName"],
    foreignKeys = [
        ForeignKey(
            entity = LabTestAndMedEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("labTestId")
        )
    ]
)
data class LabTestAndMedPhotoEntity(
    val id: String,
    val labTestId: String,
    val fileName: String,
    val note: String?,
    val fhirId: String? = null
)