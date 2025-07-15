package com.heartcare.agni.data.local.roomdb.entities.patient

import androidx.annotation.Keep
import androidx.room.Embedded
import androidx.room.Relation
import com.heartcare.agni.data.local.roomdb.entities.cvd.CVDEntity

@Keep
data class PatientAndIdentifierEntity(
    @Embedded val patientEntity: PatientEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "patientId"
    ) val identifiers: List<IdentifierEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "patientId"
    ) val cvdList: List<CVDEntity>
)