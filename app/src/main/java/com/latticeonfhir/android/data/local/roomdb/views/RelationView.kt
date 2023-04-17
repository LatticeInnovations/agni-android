package com.latticeonfhir.android.data.local.roomdb.views

import androidx.annotation.Keep
import androidx.room.DatabaseView
import com.latticeonfhir.android.data.local.enums.RelationEnum

@Keep
@DatabaseView(
    "select pat1.id as patientId , pat1.firstName as patientFirstName, pat1.middleName as patientMiddleName, pat1.lastName as patientLastName, pat2.id as relativeId ,pat2.firstName as relativeFirstName, pat2.middleName as relativeMiddleName, pat2.lastName as relativeLastName, rel.relation   as relation from RelationEntity rel \n" +
            "inner join PatientEntity pat1 on rel.fromId = pat1.id\n" +
            "inner join PatientEntity pat2 on rel.toId = pat2.id"
)
data class RelationView(
    val patientId: String,
    val patientFirstName: String,
    val patientMiddleName: String?,
    val patientLastName: String?,
    val relativeId: String,
    val relativeFirstName: String,
    val relativeMiddleName: String?,
    val relativeLastName: String?,
    val relation: RelationEnum
)
